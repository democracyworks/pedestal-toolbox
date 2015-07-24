(ns pedestal-toolbox.content-negotiation-test
  (:require [clojure.test :refer :all]
            [pedestal-toolbox.content-negotiation :refer :all]
            [pedestal-toolbox.response :refer :all]
            [cheshire.core :as json]
            [cognitect.transit :as transit])
  (:import [java.io ByteArrayOutputStream]))

(deftest negotiate-response-content-type-test
  (testing "returns JSON response when requested"
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

  (testing "returns Transit JSON response when requested"
    (let [transit-json-acceptor (negotiate-response-content-type ["application/transit+json"])
          enter (:enter transit-json-acceptor)
          leave (:leave transit-json-acceptor)]
      (testing "enter"
        (let [accept-transit-json-response {:request {:headers {"accept" "application/transit+json"}}}
              accept-any-response {:request {:headers {"accept" "*/*"}}}
              accept-edn-response {:request {:headers {"accept" "application/edn"}}}]
          (testing "adds the media-type to the request if acceptable"
            (is (= "application/transit+json" (get-in (enter accept-transit-json-response)
                                              [:request :media-type])))
            (is (= "application/transit+json" (get-in (enter accept-any-response)
                                              [:request :media-type]))))
          (testing "returns not-acceptable if there is no matching response type"
            (is (= not-acceptable (-> accept-edn-response enter :response))))))
      (testing "leave"
        (let [ctx {:request {:media-type "application/transit+json"}
                   :response {:status 200 :headers {} :body {:foo "bar"}}}
              write-transit (fn [body]
                              (let [out (ByteArrayOutputStream.)]
                                (-> out
                                    (transit/writer :json)
                                    (transit/write body))
                                (.toString out "UTF-8")))]
          (is (= "application/transit+json" (-> ctx
                                                leave
                                                (get-in [:response :headers "Content-Type"]))))
          (is (= (write-transit (get-in ctx [:response :body]))
                 (get-in (leave ctx) [:response :body])))))))

  (testing "returns Transit msgpack response when requested"
    (let [transit-msgpack-acceptor (negotiate-response-content-type ["application/transit+msgpack"])
          enter (:enter transit-msgpack-acceptor)
          leave (:leave transit-msgpack-acceptor)]
      (testing "enter"
        (let [accept-transit-msgpack-response {:request {:headers {"accept" "application/transit+msgpack"}}}
              accept-any-response {:request {:headers {"accept" "*/*"}}}
              accept-transit-json-response {:request {:headers {"accept" "application/transit+json"}}}]
          (testing "adds the media-type to the request if acceptable"
            (is (= "application/transit+msgpack" (get-in (enter accept-transit-msgpack-response)
                                              [:request :media-type])))
            (is (= "application/transit+msgpack" (get-in (enter accept-any-response)
                                              [:request :media-type]))))
          (testing "returns not-acceptable if there is no matching response type"
            (is (= not-acceptable (-> accept-transit-json-response enter :response))))))
      (testing "leave"
        (let [ctx {:request {:media-type "application/transit+msgpack"}
                   :response {:status 200 :headers {} :body {:foo "bar"}}}
              write-transit (fn [body]
                              (let [out (ByteArrayOutputStream.)]
                                (-> out
                                    (transit/writer :msgpack)
                                    (transit/write body))
                                (.toByteArray out)))]
          (is (= "application/transit+msgpack" (-> ctx
                                                   leave
                                                   (get-in [:response :headers "Content-Type"]))))
          ;; byte arrays w/ = use object identity, not value; so we use java.util.Arrays/equals instead
          (is (java.util.Arrays/equals (write-transit (get-in ctx [:response :body]))
                                       (get-in (leave ctx) [:response :body]))))))))
