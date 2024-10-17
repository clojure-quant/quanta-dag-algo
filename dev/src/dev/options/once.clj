(ns dev.options.once
  (:require
   [quanta.algo.options :as o]))

; SET OPTIONS

(def algo {:x 1
           :y 2
           :users {:w "walter"}})

(o/apply-options algo
                 {:x 5
                  [:users :w] "willy"})
;; => {:x 5, :y 2, :users {:w "willy"}}

  ;; => {:algo {:x 1, :y 2, :users {:w "walter"}}, :x 5, :users {:w "willy"}}

(o/apply-options algo
                 {:x 5
                  [:users :w] "willy"})
;; => {:x 5, :y 2, :users {:w "willy"}}
