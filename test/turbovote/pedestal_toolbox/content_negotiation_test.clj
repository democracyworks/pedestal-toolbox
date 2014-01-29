(ns turbovote.pedestal-toolbox.content-negotiation-test
  (:require [clojure.test :refer :all]
            [turbovote.pedestal-toolbox.content-negotiation :refer :all]
            [turbovote.pedestal-toolbox.response :refer [not-acceptable]]))

(deftest negotiate-content-type-test
  (let [json-acceptor (:enter (negotiate-content-type ["application/json"]))
        json-request {:request {:headers {"accept" "application/json"}}}
        star-request {:request {:headers {"accept" "*/*"}}}
        edn-request {:request {:headers {"accept" "application/edn"}}}]
    (testing "adds the media-type to the request if acceptable"
      (is (= ["application" "json"] (get-in (json-acceptor json-request)
                                         [:request :media-type])))
      (is (= ["application" "json"] (get-in (json-acceptor star-request)
                                         [:request :media-type]))))
    (testing "returns not-acceptable if there is no matching response type"
      (is (= not-acceptable (-> edn-request json-acceptor :response))))))
