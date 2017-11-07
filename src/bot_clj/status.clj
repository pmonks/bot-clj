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

(ns bot-clj.status
  (:require [clojure.string        :as s]
            [clojure.tools.logging :as log]
            [mount.core            :as mnt :refer [defstate]]
            [bot-clj.config        :as cfg]))

(defstate jolokia-server
          :start (let [server (org.jolokia.jvmagent.JolokiaServer.
                                (org.jolokia.jvmagent.JolokiaServerConfig.
                                  (into {"logHandlerClass" "org.jolokia.util.JulLogHandler"} (:jolokia-config cfg/config)))  ; Hardcode Jolokia to use JUL
                                false)]
                   (.start server)
                   server)
          :stop  (.stop ^org.jolokia.jvmagent.JolokiaServer jolokia-server))
