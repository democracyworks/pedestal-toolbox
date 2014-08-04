(defproject turbovote.pedestal-toolbox "0.5.0-SNAPSHOT"
  :description "Pedestal service helpers"
  :url "http://www.github.com/turbovote/pedestal-toolbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [io.pedestal/pedestal.service "0.3.0"]
                 [liberator "0.11.0"]
                 [ring/ring-core "1.3.0"]
                 [cheshire "5.3.1"]
                 [prismatic/schema "0.2.1"]
                 [clj-time "0.7.0"]])
