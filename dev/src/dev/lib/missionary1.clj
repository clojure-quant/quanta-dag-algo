(ns dev.lib.missionary1
  (:require
   [missionary.core :as m]))

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

