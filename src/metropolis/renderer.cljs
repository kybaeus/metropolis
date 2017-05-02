(ns metropolis.renderer
  (:require [cljsjs.three]))

(defn getAspect []
  (/ (.-innerWidth js/window)
     (.-innerHeight js/window))) ; Aspect
