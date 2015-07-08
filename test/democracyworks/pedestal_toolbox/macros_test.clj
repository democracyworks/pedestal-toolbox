(ns democracyworks.pedestal-toolbox.macros-test
  (:require [clojure.test :refer :all]
            [democracyworks.pedestal-toolbox.macros :refer :all]))

(deftest let-or-reply-test
  (let [context {:request {:foo 1 :bar "hi"}}]
    (testing "with empty bindings, just does exprs"
      (let [side-effects (atom 0)
            new-context (let-or-reply context []
                                      (swap! side-effects inc)
                                      (assoc-in context [:request :baz] :good))]
        (is (= (assoc-in context [:request :baz] :good) new-context))
        (is (= 1 @side-effects))))
    (testing "with multiple sets of bindings that all succeed"
      (let [good-side-effects (atom 0)
            fail-side-effects (atom 0)
            new-context (let-or-reply context
                                      [foo (get-in context [:request :foo])
                                           (do (swap! fail-side-effects inc)
                                               :failed-foo)
                                       bar (get-in context [:request :bar])
                                           (do (swap! fail-side-effects inc)
                                               :failed-bar)]
                                      (swap! good-side-effects inc)
                                      (assoc-in context [:request :baz] :great))]
        (is (= (assoc-in context [:request :baz] :great) new-context))
        (is (zero? @fail-side-effects))
        (is (= 1 @good-side-effects))))
    (testing "with multiple sets of bindings with one that fails"
      (let [good-side-effects (atom 0)
            fail-side-effects (atom 0)
            new-context (let-or-reply context
                                      [foo (get-in context [:request :foo])
                                           (do (swap! fail-side-effects inc)
                                               :failed-foo)
                                       nope (:nope context)
                                            (do (swap! fail-side-effects inc)
                                                :failed-nope)
                                       bar (get-in context [:request :bar])
                                           (do (swap! fail-side-effects inc)
                                               :failed-bar)]
                                      (swap! good-side-effects inc)
                                      (assoc-in context [:request :baz] :wont-see))]
        (is (= (assoc context :response :failed-nope) new-context))
        (is (zero? @good-side-effects))
        (is (= 1 @fail-side-effects))))))
