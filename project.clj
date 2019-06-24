(defproject democracyworks/pedestal-toolbox "0.7.1"
  :description "Pedestal service helpers"
  :url "http://www.github.com/democracyworks/pedestal-toolbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [liberator "0.15.3"]
                 [ring/ring-core "1.7.1"]
                 [cheshire "5.8.1"]
                 [prismatic/schema "1.1.11"]
                 [clj-time "0.15.1"]
                 [com.cognitect/transit-clj "0.8.313"]]
  :deploy-repositories [["releases" :clojars]])
