(ns democracyworks.pedestal-toolbox.content-negotiation-test
  (:require [clojure.test :refer :all]
            [democracyworks.pedestal-toolbox.content-negotiation :refer :all]
            [democracyworks.pedestal-toolbox.response :refer :all]
            [cheshire.core :as json]))

(deftest negotiate-response-content-type-test
  (let [json-acceptor (negotiate-response-content-type ["application/json"])
        enter (:enter json-acceptor)
        leave (:leave json-acceptor)]
    (testing "enter"
      (let [accept-json-response {:request {:headers {"accept" "application/json"}}}
            accept-any-response {:request {:headers {"accept" "*/*"}}}
            accept-edn-response {:request {:headers {"accept" "application/edn"}}}]
        (testing "adds the media-type to the request if acceptable"
          (is (= "application/json" (get-in (enter accept-json-response)
                                            [:request :media-type])))
          (is (= "application/json" (get-in (enter accept-any-response)
                                            [:request :media-type]))))
        (testing "returns not-acceptable if there is no matching response type"
          (is (= not-acceptable (-> accept-edn-response enter :response))))))
    (testing "leave"
      (let [ctx {:request {:media-type "application/json"}
                 :response {:status 200 :headers {} :body {:foo "bar"}}}]
        (is (= "application/json" (-> ctx
                                      leave
                                      (get-in [:response :headers "Content-Type"]))))
        (is (= (json/generate-string (get-in ctx [:response :body]))
               (get-in (leave ctx) [:response :body])))))))
