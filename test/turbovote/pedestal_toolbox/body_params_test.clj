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

(deftest coerce-body-params-test
  (let [params {:a 1}
        edn-ctx {:request {:edn-params params}}
        json-ctx {:request {:json-params params}}
        no-params-ctx {:request {}}]
    (testing "with no coercions"
      (let [enter (:enter (coerce-body-params))]
        (is (= params (-> edn-ctx
                          enter
                          :request
                          :body-params)))
        (is (= params (-> json-ctx
                          enter
                          :request
                          :body-params)))
        (is (= no-params-ctx (enter no-params-ctx)))))
    (testing "with coercions"
      (let [enter (:enter (coerce-body-params
                           {:edn-params (fn [m] (update-in m [:a] inc))
                            :json-params (fn [m] (update-in m [:a] dec))}))]
        (is (= {:a 2} (-> edn-ctx
                          enter
                          :request
                          :body-params)))
        (is (= {:a 0} (-> json-ctx
                          enter
                          :request
                          :body-params)))
        (is (= no-params-ctx (enter no-params-ctx)))))))
