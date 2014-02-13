(defproject turbovote.pedestal-toolbox "0.3.0-SNAPSHOT"
  :description "Pedestal service helpers"
  :url "http://www.github.com/turbovote/pedestal-toolbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [io.pedestal/pedestal.service "0.2.2"]
                 [liberator "0.10.0"]
                 [ring/ring-core "1.2.1"]
                 [cheshire "5.3.1"]
                 [prismatic/schema "0.2.0"]
                 [clj-time "0.6.0"]])
