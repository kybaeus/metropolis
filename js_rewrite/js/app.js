
container = document.createElement( 'div' )
document.body.appendChild( container )

camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 1, 3000 )
camera.position.set( 0, 20, 35 )

renderer = new THREE.WebGLRenderer()
renderer.setClearColor( 0x000000 )
renderer.setPixelRatio( window.devicePixelRatio )
renderer.setSize( window.innerWidth, window.innerHeight )

container.appendChild( renderer.domElement )
controls = new THREE.OrbitControls( camera, renderer.domElement )

controls.enableDamping = true

scene = new THREE.Scene()

redMaterial = new THREE.MeshBasicMaterial({color:0xF06565})
boxGeometry = new THREE.BoxGeometry( 1, 1, 1 )
boxObject = new THREE.Mesh( boxGeometry, redMaterial )

boxObject.position.z = 0

scene.add(boxObject)

blueMaterial = new THREE.MeshBasicMaterial({color:0x006565})
boxObject = new THREE.Mesh( boxGeometry, blueMaterial )
boxObject.position.z = 1

scene.add(boxObject)

let cFragmentShader = (`

  void main() {
		vec2 uv = gl_FragCoord.xy / resolution.xy;
	  gl_FragColor = texture2D( testTexture, uv ) + 0.1;
  }

`)

let render = () => {
  renderer.render( scene, camera )
}

let animate = () => {
		requestAnimationFrame( animate )
		controls.update()
		render()
}

const BOUNDS = 100
const BOUNDS_HALF = 50
let fillArray = (texture) => {
	var theArray = texture.image.data;
  for ( var k = 0, kl = theArray.length; k < kl; k += 4 ) {
		var x = Math.random() * BOUNDS - BOUNDS_HALF;
		var y = Math.random() * BOUNDS - BOUNDS_HALF;
		var z = Math.random() * BOUNDS - BOUNDS_HALF;
		theArray[ k + 0 ] = x;
		theArray[ k + 1 ] = y;
		theArray[ k + 2 ] = z;
		theArray[ k + 3 ] = 1;
	}
  return theArray


}

let gpuCompute = new GPUComputationRenderer( 1024, 1024, renderer )
let textureA = gpuCompute.createTexture()
let filledTextureA = fillArray(textureA)

let testVar = gpuCompute.addVariable( "testTexture", cFragmentShader, filledTextureA)

gpuCompute.setVariableDependencies( testVar, [testVar] )

gpuCompute.init()
gpuCompute.compute()

tex = gpuCompute.getCurrentRenderTarget( testVar ).texture

compMaterial = new THREE.MeshBasicMaterial({map:tex})
boxGeometry2 = new THREE.BoxGeometry( 1, 1, 1 )
mainBoxObject = new THREE.Mesh(boxGeometry2,compMaterial)
scene.add(mainBoxObject)
animate()
