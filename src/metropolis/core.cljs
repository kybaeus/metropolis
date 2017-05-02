(ns metropolis.core
  (:require [metropolis.shaders :as s]
            [metropolis.renderer :as renderer]
            cljsjs.three))

(enable-console-print!)

(println renderer/getAspect)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn teardown []
  (when-let [renderer (:renderer @app-state)]
    (.log js/console "Removing: " (.-xxid renderer))
    (.removeChild (.-body js/document) (.-domElement renderer))
    (swap! app-state dissoc :renderer)))

(defn init []

  ;;First initiate the basic elements of a THREE scene
  (let [scene    (js/THREE.Scene.)
        camera (js/THREE.PerspectiveCamera. 75 ; Angle
                                            (renderer/getAspect)
                                            0.1 ; Near
                                            1000) ; Far
        box      (js/THREE.BoxGeometry. 1 1 1)

        mat      (js/THREE.MeshBasicMaterial.
                    (clj->js {:color     0xFFF000
                              :wireframe false}))

        testShader (js/THREE.ShaderMaterial. (clj->js {;;:uniforms {:time 10}
                                                       :vertexShader s/vertexShader
                                                       :fragmentShader s/fragmentShader}))

        mesh     (js/THREE.Mesh. box testShader)
        renderer (js/THREE.WebGLRenderer.)]

    ;;Change the starting position of cube and camera
    (aset camera "name" "camera")
    (aset camera "position" "z" 3)

    (aset mesh "rotation" "x" 45)
    (aset mesh "rotation" "y" 0)

    (.setSize renderer (.-innerWidth js/window)
                       (.-innerHeight js/window))

    ;;Add camera, mesh and box to scene and then that to DOM node.
    (.add scene camera)
    (.add scene mesh)
    (.appendChild js/document.body (.-domElement renderer))

    (set! (.. camera -position -z) 3)
    ;Kick off the animation loop updating
    (swap! app-state #(assoc %
                             :renderer renderer))
    (defn render []
      (aset mesh "rotation" "y" (+ 0.01 (.-y (.-rotation mesh))))
      (.render renderer scene camera))


    (defn animate []
      (.requestAnimationFrame js/window animate)
      (render))

    (animate)))

(defn on-js-reload []
  (teardown)
  (init)

  (println "HI"))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
