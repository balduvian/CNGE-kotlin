uniform sampler2D sampler;

in vec2 texCoords;
in vec2 coords;
out vec4 outColor;

uniform float ratio;
uniform float scanLines;
uniform float scanOffset;
uniform vec2 redOffset;
uniform vec2 greOffset;
uniform vec2 bluOffset;
uniform float seed;

#define PI 3.1415926538

const float rand0 = mix(6.2823, 25.9282, seed);
const float rand1 = mix(2.9702, 13.8027, seed);
const float rand2 = mix(29083.0204, 89083.5105, seed);

float rand(vec2 n) {
	return fract(sin(dot(n, vec2(rand0, rand1))) * rand2);
}

float noise(vec2 p){
	vec2 ip = floor(p);
	vec2 u = fract(p);
	u = u*u*(3.0-2.0*u);

	float res = mix(
	mix(rand(ip),rand(ip+vec2(1.0,0.0)),u.x),
	mix(rand(ip+vec2(0.0,1.0)),rand(ip+vec2(1.0,1.0)),u.x),u.y);
	return res*res;
}

const float scanOffset0 = rand(vec2(8493.9372, 1382.3001));
const float scanOffset1 = rand(vec2(2872.6119, 9022.2128));
const float scanOffset2 = rand(vec2(3346.3321, 6182.9820));

float scanModifier(float along) {
	return (
		sin(2 * PI * (along - scanOffset0)) +
		sin(4 * PI * (along - scanOffset1)) +
		sin(8 * PI * (along - scanOffset2))
	) / 3;
}

void main() {
	float distance = length(texCoords - vec2(0.5, 0.5));
	float vignetteAlong = smoothstep(0.5, sqrt(0.5), distance);

	float vignetteColor = 1 - vignetteAlong;

	float scanX = sin(coords.y * (2 * PI * scanLines)) * scanOffset * scanModifier(coords.y);

	float red = texture(sampler, texCoords + vec2(scanX / ratio, 0) + redOffset * vec2(1 / ratio, 1)).x;
	float gre = texture(sampler, texCoords + vec2(scanX / ratio, 0) + greOffset * vec2(1 / ratio, 1)).y;
	float blu = texture(sampler, texCoords + vec2(scanX / ratio, 0) + bluOffset * vec2(1 / ratio, 1)).z;

	float addNoise = (noise(coords * 300) * 0.2) - 0.1;

	outColor = vec4(vec3(red + addNoise, gre + addNoise, blu + addNoise) * vignetteColor, 1);
}
