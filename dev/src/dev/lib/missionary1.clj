(ns dev.lib.missionary1
  (:require
   [missionary.core :as m])
  (:import (missionary Cancelled)))

(defn sleep-emit [delays]
  (m/reductions {} 0
                (m/ap (let [n (m/?> (m/seed delays))]
                        (m/? (m/sleep n n))))))

(m/? (->> (sleep-emit [1 5 10 2])
          (m/reduce conj)))

(m/? (->> (sleep-emit [1 5 10 2])
          (m/eduction (take 2))
          (m/reduce conj)))

(defn delay-each [delay input]
  (m/ap (m/? (m/sleep delay (m/?> input)))))

(m/? (->> (m/latest vector
                    (sleep-emit [24 79 67 34])
                    (sleep-emit [86 12 37 93]))
          ;(delay-each 50)
          (m/reduce conj)))

(def non-0-sleep-emit
  (m/eduction (remove #(= 0 %)) (sleep-emit [1 5 10 2])))

(m/? (->> non-0-sleep-emit
          (m/eduction (take 2))
          (m/reduce conj)))

(m/? (->> (m/latest vector
                    non-0-sleep-emit
                    (sleep-emit [86 12 37 93]))
          ;(delay-each 50)
          (m/reduce conj)))

(defn run! [f]
  (m/?
   (m/reduce (fn [r v]
               v) nil
             (m/eduction (remove nil?)
                         (take 1) f))))

(def a (m/signal (m/latest vector (m/seed [1]) (m/seed [2]))))

(run! a)
;; => [1 2]

(defn mult [[a b]]
  (m/sp (* a b)))

(def c (m/signal (m/ap (let [v (m/?> a)]
                         (println "args: " v)
                         (m/? (mult v))))))

(run! c)
;; => 2

(defn slow-mult [[a b]]
  (m/sp (m/? (m/sleep 500))
        (* a b)))

(def d (->> (m/ap
             (m/amb nil)
             (let [v (m/?> a)]
               (try
                 (println "args: " v)
                 (m/? (slow-mult v))

                 (catch Cancelled _))))
            (m/reductions (fn [r v] v) nil)
            (m/relieve {})
            (m/signal)))

(run! d)
;; => [{}, true, true, true, #error {
;;     :cause "Sleep cancelled."
;;     :via
;;     [{:type missionary.Cancelled
;;       :message "Sleep cancelled."}]
;;     :trace
;;     []}]



