(ns turbovote.pedestal-toolbox.params-test
  (:require [clojure.test :refer :all]
            [io.pedestal.service.http.body-params :as body-params]
            [turbovote.pedestal-toolbox.params :refer :all]
            [turbovote.pedestal-toolbox.response :refer [bad-request]]))

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

(deftest validate-body-params-test
  (let [params {:a 1 :b "abc"}
        ctx {:request {:body-params params}}
        matching-schema {:a Long :b String}
        mismatched-schema {:a String :c String}]
    (testing "with a body that matches the schema"
      (let [enter (:enter (validate-body-params matching-schema))]
        (is (= ctx (enter ctx)))))
    (testing "with a body that does not match the schema"
      (let [enter (:enter (validate-body-params mismatched-schema))]
        (is (= 400 (-> ctx
                       enter
                       :response
                       :status))))))
  (let [schema {:date java.util.Date
                :uuid java.util.UUID}
        enter (:enter (validate-body-params schema))]
    (testing "without needing coercion"
      (let [ctx {:request {:body-params {:date (java.util.Date.)
                                         :uuid (java.util.UUID/randomUUID)}}}]
        (is (= ctx (enter ctx)))))
    (testing "with successful coercion"
      (let [date #inst "1977-08-27"
            uuid (java.util.UUID/randomUUID)
            ctx {:request {:body-params {:date "1977-08-27"
                                         :uuid (str uuid)}}}]
        (is (= {:request {:body-params {:date date :uuid uuid}}}
               (enter ctx)))))
    (testing "with unsuccessful coercion"
      (is (= 400
             (-> {:request {:body-params {:date "4 score and 20 years ago"
                                          :uuid "this is not a UUID!"}}}
                 enter
                 (get-in [:response :status])))))))
