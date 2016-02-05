(defproject democracyworks/pedestal-toolbox "0.7.0"
  :description "Pedestal service helpers"
  :url "http://www.github.com/democracyworks/pedestal-toolbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [io.pedestal/pedestal.service "0.4.1"]
                 [liberator "0.14.0"]
                 [ring/ring-core "1.4.0"]
                 [cheshire "5.5.0"]
                 [prismatic/schema "1.0.4"]
                 [clj-time "0.11.0"]
                 [com.cognitect/transit-clj "0.8.285"]]
  :deploy-repositories [["releases" :clojars]])
