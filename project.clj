;
; Copyright 2017 Fintech Open Source Foundation
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

(def jackson-version "2.9.4")
(def jersey-version  "2.25.1")     ; Note: upgrading past 2.25.x breaks Jackson

(defproject org.finos.symphony/bot-clj "0.1.0-SNAPSHOT"
  :description      "A bot that looks for Clojure code in messages, and executes it, returning the result."
  :url              "https://github.com/pmonks/bot-clj"
  :license          {:spdx-license-identifier "Apache-2.0"
                     :name                    "Apache License, Version 2.0"
                     :url                     "http://www.apache.org/licenses/LICENSE-2.0"}
  :min-lein-version "2.8.1"
  :repositories     [["sonatype-snapshots" {:url "https://oss.sonatype.org/content/groups/public" :snapshots true}]
                     ["jitpack"            {:url "https://jitpack.io"}]]
  :plugins          [
                      [org.noisesmith/git-info-edn "0.2.2"]
                    ]
  :dependencies     [
                      [org.clojure/clojure              "1.10.1"]
                      [org.apache.commons/commons-lang3 "3.10"]
                      [aero                             "1.1.6"]
                      [mount                            "0.1.16"]
                      [org.clojure/tools.cli            "1.0.194"]
                      [org.clojure/tools.logging        "1.0.0"]
                      [ch.qos.logback/logback-classic   "1.2.3"]
                      [org.slf4j/jcl-over-slf4j         "1.7.30"]
                      [org.slf4j/log4j-over-slf4j       "1.7.30"]
                      [org.slf4j/jul-to-slf4j           "1.7.30"]
                      [org.jolokia/jolokia-jvm          "1.6.2"]
                      [org.jolokia/jolokia-jvm          "1.6.2" :classifier "agent"]
                      [clj-time                         "0.15.2"]
                      [juji/clojail                     "1.0.9" :exclusions [org.clojure/clojure]]
                      [org.clojars.pmonks/clj-2253      "0.1.0" :exclusions [org.clojure/clojure]]
                      [org.symphonyoss/clj-symphony     "1.0.1" :exclusions [org.clojure/clojure
                                                                             org.slf4j/slf4j-log4j12]]

                      ; The following dependencies are inherited but have conflicting versions, so we "pin" the versions here
                      [com.fasterxml.jackson.core/jackson-core                      ~jackson-version]
                      [com.fasterxml.jackson.core/jackson-databind                  ~jackson-version]
                      [com.fasterxml.jackson.core/jackson-annotations               ~jackson-version]
                      [com.fasterxml.jackson.jaxrs/jackson-jaxrs-base               ~jackson-version]
                      [com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider      ~jackson-version]
                      [com.fasterxml.jackson.dataformat/jackson-dataformat-yaml     ~jackson-version]
                      [com.fasterxml.jackson.dataformat/jackson-dataformat-cbor     ~jackson-version]
                      [com.fasterxml.jackson.dataformat/jackson-dataformat-smile    ~jackson-version]
                      [com.fasterxml.jackson.datatype/jackson-datatype-jsr310       ~jackson-version]
                      [com.fasterxml.jackson.module/jackson-module-jaxb-annotations ~jackson-version]
                      [org.glassfish.jersey.core/jersey-client                      ~jersey-version]
                      [org.glassfish.jersey.core/jersey-common                      ~jersey-version]
                      [org.glassfish.jersey.media/jersey-media-json-jackson         ~jersey-version]
                      [joda-time/joda-time                                          "2.10.6"]
                      [org.hamcrest/hamcrest-core                                   "2.2"]
                    ]
  :profiles         {:dev {:dependencies [[midje         "1.9.9"]]
                           :plugins      [[lein-midje    "3.2.1"]
                                          [lein-licenses "0.2.2"]]}
                     :uberjar {:aot          :all
                               :uberjar-name "bot-clj-standalone.jar"}}
  :main             bot-clj.main)
