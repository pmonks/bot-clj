;
; Copyright © 2017 Symphony Software Foundation
; SPDX-License-Identifier: Apache-2.0
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;

(ns bot-clj.eval
  (:require [clojure.string               :as s]
            [clojure.pprint               :as pp]
            [clojure.tools.logging        :as log]
            [mount.core                   :as mnt :refer [defstate]]
            [clojail.core                 :as cjc]
            [clojail.testers              :as cjt]
            [clj-symphony.user            :as syu]
            [clj-symphony.message         :as sym]
            [clj-symphony.stream          :as sys]
            [clj-symphony.user-connection :as syuc]
            [bot-clj.config               :as cfg]
            [bot-clj.connection           :as cnxn]))

(defstate evaluation-timeout-ms
          :start (if-let [result (:evaluation-timeout cfg/config)]
                   (* 1000 result)
                   (* 1000 10)))    ; Default is 10 seconds

(defstate sandbox
          :start (cjc/sandbox (conj cjt/secure-tester-without-def
                                    (cjt/blanket "mount")
                                    (cjt/blanket "aero")
                                    (cjt/blanket "bot-clj")
                                    (cjt/blanket "clj-symphony"))
                              :timeout evaluation-timeout-ms))

(defn reset-sandbox!
  []
  (mnt/stop #'bot-clj.eval/sandbox)
  (mnt/start #'bot-clj.eval/sandbox))

(defn- eval-in-sandbox
  "Evaluate the given string in the secure sandbox, returning a map containing:
  :successful - a boolean indicating whether the forms were successfully executed or not
  :input      - the string that was evaluated
  :form       - the form the string was parsed into
  :result     - the result of evaluating the form (will be the exception message, if :successful is false)
  :out        - any output written to stdout
  :err        - any output written to stderr
  :exception  - the exception object, if one was thrown"
  [s]
  (try
    (with-open [out (java.io.StringWriter.)
              err (java.io.StringWriter.)]
      (let [form   (binding [*read-eval* false] (read-string s))
            result (sandbox form {#'*out* out #'*err* err})]
        {
          :successful true
          :input      s
          :form       form
          :result     result
          :result-str (with-out-str (pp/pprint result))
          :out        (str out)
          :err        (str err)
        }))
    (catch Exception e
      {
        :successful    false
        :input         s
        :error-message (.getMessage e)
      })))

(defn- send-evaluation-result-message!
  [stream-id eval-result]
  (let [message (str "<messageML>"
                     (if (not (s/blank? (:err eval-result)))
                       (str "<pre>" (:err eval-result) "</pre>"))
                     (if (not (s/blank? (:out eval-result)))
                       (str "<pre>" (:out eval-result) "</pre>"))
                     (if (not (s/blank? (:result-str eval-result)))
                       (str "<pre>" (:result-str eval-result) "</pre>"))
                     (if (not (s/blank? (:error-message eval-result)))
                       (str "<pre>" (:error-message eval-result) "</pre>"))
                     "</messageML>")]
    (sym/send-message! cnxn/symphony-connection stream-id message)))

(defn evaluate-message-and-post-result!!
  "If the given message starts with the ` character, evaluates the rest of the message as Clojure code in a secure sandbox and posts the response."
  [message-id stream-id text]
  (when-let [plain-text (s/trim (sym/to-plain-text text))]
    (log/debug "Plain text is" plain-text)
    (if (s/starts-with? plain-text "`")
      (let [text-to-eval (subs plain-text 1)
            _            (log/debug "Evaluating" text-to-eval)
            eval-result  (eval-in-sandbox text-to-eval)]
        (send-evaluation-result-message! stream-id eval-result)))))
