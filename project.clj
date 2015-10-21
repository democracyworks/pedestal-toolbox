(defproject democracyworks/pedestal-toolbox "0.6.2-SNAPSHOT"
  :description "Pedestal service helpers"
  :url "http://www.github.com/democracyworks/pedestal-toolbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [io.pedestal/pedestal.service "0.4.0"]
                 [liberator "0.13"]
                 [ring/ring-core "1.4.0"]
                 [cheshire "5.5.0"]
                 [prismatic/schema "0.4.3"]
                 [clj-time "0.10.0"]
                 [com.cognitect/transit-clj "0.8.275"]]
  :deploy-repositories [["releases" :clojars]])
