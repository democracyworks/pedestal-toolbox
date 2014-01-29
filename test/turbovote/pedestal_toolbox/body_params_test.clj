(ns turbovote.pedestal-toolbox.body-params-test
  (:require [clojure.test :refer :all]
            [io.pedestal.service.http.body-params :as body-params]
            [turbovote.pedestal-toolbox.body-params :refer :all]))

(deftest body-params-test
  (let [make-request (fn [body]
                       {:request {:content-type "application/edn"
                                  :body (java.io.ByteArrayInputStream.
                                         (.getBytes body))}})
        good-body "{:a 1}"
        bad-body  "{:a  }"]
    (testing "Acts just like body-params/body-params on good inputs"
      (is (= (read-string good-body)
             (-> good-body
                 make-request
                 ((:enter (body-params/body-params)))
                 (get-in [:request :edn-params]))
             (-> good-body
                 make-request
                 ((:enter body-params))
                 (get-in [:request :edn-params])))))
    (testing "Returns a 400 (not a 500!) on a bad request"
      (is (= 400 (-> bad-body
                     make-request
                     ((:enter body-params))
                     (get-in [:response :status])))))))
