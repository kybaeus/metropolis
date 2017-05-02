(ns metropolis.shaders)

(def vertexShader "
  void main() {
  	vec4 modelViewPosition = modelViewMatrix * vec4(position, 1.0);
  	gl_Position = projectionMatrix * modelViewPosition;
  }
")

(def fragmentShader "
  void main() {
    gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);
  }
")
