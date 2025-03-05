(ns quanta.dag.util
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [missionary.core :as m]))

(defn take-first-non-nil [f]
  ; flows dont implement deref
  (m/eduction
   (remove nil?)
   (take 1)
   f))

(defn current-v
  "gets the first non-nil value from the flow"
  [f]
  (m/reduce (fn [r v]
              ;(println "current v: " v " r: " r)
              v) nil
            (take-first-non-nil f)))

(defn cont
  "converts a discrete flow to a continuous flow. 
    returns nil in the beginning."
  [flow]
  (->> flow
       (m/reductions (fn [r v]
                       (if v v r)) nil)
       (m/relieve {})))

(defn first-match [predicate flow]
  (m/reduce (fn [_r v]
              (info "first-match check: " v)
              (when (predicate v)
                (info "success! returning: " v)
                (reduced v)))
            nil
            flow))
