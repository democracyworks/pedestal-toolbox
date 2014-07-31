(ns turbovote.pedestal-toolbox.content-negotiation-test
  (:require [clojure.test :refer :all]
            [turbovote.pedestal-toolbox.content-negotiation :refer :all]
            [turbovote.pedestal-toolbox.response :refer :all]
            [cheshire.core :as json]))

(deftest negotiate-content-type-test
  (let [json-acceptor (negotiate-content-type ["application/json"])
        enter (:enter json-acceptor)
        leave (:leave json-acceptor)]
    (testing "enter"
      (let [json-response {:request {:headers {"accept" "application/json"}}}
            star-response {:request {:headers {"accept" "*/*"}}}
            edn-response {:request {:headers {"accept" "application/edn"}}}
            json-request {:request {:headers {"accept" "application/json"
                                              "content-type" "application/json"}}}
            edn-request {:request {:headers {"accept" "application/edn"
                                             "content-type" "application/edn"}}}]
        (testing "adds the media-type to the request if acceptable"
          (is (= "application/json" (get-in (enter json-response)
                                            [:request :media-type])))
          (is (= "application/json" (get-in (enter star-response)
                                            [:request :media-type])))
          (is (= "application/json" (get-in (enter json-request)
                                            [:request :media-type]))))
        (testing "returns unsupported media type if no matching request type"
          (is (= unsupported-media-type (-> edn-request enter :response))))
        (testing "returns not-acceptable if there is no matching response type"
          (is (= not-acceptable (-> edn-response enter :response))))))
    (testing "leave"
      (let [ctx {:request {:media-type "application/json"}
                 :response {:status 200 :headers {} :body {:foo "bar"}}}]
        (is (= "application/json" (-> ctx
                                      leave
                                      (get-in [:response :headers "Content-Type"]))))
        (is (= (json/generate-string (get-in ctx [:response :body]))
               (get-in (leave ctx) [:response :body])))))))
