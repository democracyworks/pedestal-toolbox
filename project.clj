(defproject democracyworks/pedestal-toolbox "0.7.1-SNAPSHOT"
  :description "Pedestal service helpers"
  :url "http://www.github.com/democracyworks/pedestal-toolbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [io.pedestal/pedestal.service "0.5.3"]
                 [liberator "0.15.1"]
                 [ring/ring-core "1.6.3"]
                 [cheshire "5.8.0"]
                 [prismatic/schema "1.1.7"]
                 [clj-time "0.14.0"]
                 [com.cognitect/transit-clj "0.8.300"]]
  :deploy-repositories [["releases" :clojars]])
