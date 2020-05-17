package com.spacegeneration;

/**
 * This is NOT my own work and is actually an opensource Github library that
 * is designed for noise generation.
 * Github Library: https://github.com/Auburns/FastNoise_Java
 * This library is available to be used by anyone and I am not taking any credit for any
 * of the work that is done in this.
 */

public class FastNoise {
	public enum NoiseType {Value, ValueFractal, Perlin, PerlinFractal, Simplex, SimplexFractal, Cellular, WhiteNoise, Cubic, CubicFractal}
	public enum Interp {Linear, Hermite, Quintic}
	public enum FractalType {FBM, Billow, RigidMulti}
	public enum CellularDistanceFunction {Euclidean, Manhattan, Natural}
	public enum CellularReturnType {CellValue, NoiseLookup, Distance, Distance2, Distance2Add, Distance2Sub, Distance2Mul, Distance2Div}

	private int m_seed = 1337;
	private float m_frequency = (float) 0.01;
	private Interp m_interp = Interp.Quintic;
	private NoiseType m_noiseType = NoiseType.Simplex;

	private int m_octaves = 3;
	private float m_lacunarity = (float) 2.0;
	private float m_gain = (float) 0.5;
	private FractalType m_fractalType = FractalType.FBM;

	private float m_fractalBounding;

	public FastNoise() {
		this(1337);
	}

	public FastNoise(int seed) {
		m_seed = seed;
		CalculateFractalBounding();
	}

	// Returns the seed used by this object
	public int GetSeed() {
		return m_seed;
	}

	// Sets seed used for all noise types
	// Default: 1337
	public void SetSeed(int seed) {
		m_seed = seed;
	}

	// Sets frequency for all noise types
	// Default: 0.01
	public void SetFrequency(float frequency) {
		m_frequency = frequency;
	}

	// Changes the interpolation method used to smooth between noise values
	// Possible interpolation methods (lowest to highest quality) :
	// - Linear
	// - Hermite
	// - Quintic
	// Used in Value, Gradient Noise and Position Perturbing
	// Default: Quintic
	public void SetInterp(Interp interp) {
		m_interp = interp;
	}

	// Sets noise return type of GetNoise(...)
	// Default: Simplex
	public void SetNoiseType(NoiseType noiseType) {
		m_noiseType = noiseType;
	}

	// Sets octave count for all fractal noise types
	// Default: 3
	public void SetFractalOctaves(int octaves) {
		m_octaves = octaves;
		CalculateFractalBounding();
	}

	// Sets octave lacunarity for all fractal noise types
	// Default: 2.0
	public void SetFractalLacunarity(float lacunarity) {
		m_lacunarity = lacunarity;
	}

	// Sets octave gain for all fractal noise types
	// Default: 0.5
	public void SetFractalGain(float gain) {
		m_gain = gain;
		CalculateFractalBounding();
	}

	// Sets method for combining octaves in all fractal noise types
	// Default: FBM
	public void SetFractalType(FractalType fractalType) {
		m_fractalType = fractalType;
	}

	private static class Float2 {
		public final float x, y;

		public Float2(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	private static class Float3 {
		public final float x, y, z;

		public Float3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	private static final Float2[] GRAD_2D = {
		new Float2(-1, -1), new Float2(1, -1), new Float2(-1, 1), new Float2(1, 1),
		new Float2(0, -1), new Float2(-1, 0), new Float2(0, 1), new Float2(1, 0),
	};

	private static final Float3[] GRAD_3D = {
		new Float3(1, 1, 0), new Float3(-1, 1, 0), new Float3(1, -1, 0), new Float3(-1, -1, 0),
		new Float3(1, 0, 1), new Float3(-1, 0, 1), new Float3(1, 0, -1), new Float3(-1, 0, -1),
		new Float3(0, 1, 1), new Float3(0, -1, 1), new Float3(0, 1, -1), new Float3(0, -1, -1),
		new Float3(1, 1, 0), new Float3(0, -1, 1), new Float3(-1, 1, 0), new Float3(0, -1, -1),
	};

	private static int FastFloor(float f) {
		return (f >= 0 ? (int) f : (int) f - 1);
	}

	private static float Lerp(float a, float b, float t) {
		return a + t * (b - a);
	}


	private static float InterpHermiteFunc(float t) {
		return t * t * (3 - 2 * t);
	}


	private static float InterpQuinticFunc(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private void CalculateFractalBounding() {
		float amp = m_gain;
		float ampFractal = 1;
		for (int i = 1; i < m_octaves; i++) {
			ampFractal += amp;
			amp *= m_gain;
		}
		m_fractalBounding = 1 / ampFractal;
	}

	// Hashing
	private final static int X_PRIME = 1619;
	private final static int Y_PRIME = 31337;
	private final static int Z_PRIME = 6971;
	private final static int W_PRIME = 1013;

	private static float ValCoord2D(int seed, int x, int y) {
		int n = seed;
		n ^= X_PRIME * x;
		n ^= Y_PRIME * y;

		return (n * n * n * 60493) / (float) 2147483648.0;
	}

	private static float ValCoord3D(int seed, int x, int y, int z) {
		int n = seed;
		n ^= X_PRIME * x;
		n ^= Y_PRIME * y;
		n ^= Z_PRIME * z;

		return (n * n * n * 60493) / (float) 2147483648.0;
	}

	private static float ValCoord4D(int seed, int x, int y, int z, int w) {
		int n = seed;
		n ^= X_PRIME * x;
		n ^= Y_PRIME * y;
		n ^= Z_PRIME * z;
		n ^= W_PRIME * w;

		return (n * n * n * 60493) / (float) 2147483648.0;
	}

	private static float GradCoord2D(int seed, int x, int y, float xd, float yd) {
		int hash = seed;
		hash ^= X_PRIME * x;
		hash ^= Y_PRIME * y;

		hash = hash * hash * hash * 60493;
		hash = (hash >> 13) ^ hash;

		Float2 g = GRAD_2D[hash & 7];

		return xd * g.x + yd * g.y;
	}

	private static float GradCoord3D(int seed, int x, int y, int z, float xd, float yd, float zd) {
		int hash = seed;
		hash ^= X_PRIME * x;
		hash ^= Y_PRIME * y;
		hash ^= Z_PRIME * z;

		hash = hash * hash * hash * 60493;
		hash = (hash >> 13) ^ hash;

		Float3 g = GRAD_3D[hash & 15];

		return xd * g.x + yd * g.y + zd * g.z;
	}

	public float GetNoise(float x, float y, float z) {
		x *= m_frequency;
		y *= m_frequency;
		z *= m_frequency;

		switch (m_noiseType) {
			case Value:
				return SingleValue(m_seed, x, y, z);
			case ValueFractal:
				switch (m_fractalType) {
					case FBM:
						return SingleValueFractalFBM(x, y, z);
					case Billow:
						return SingleValueFractalBillow(x, y, z);
					case RigidMulti:
						return SingleValueFractalRigidMulti(x, y, z);
					default:
						return 0;
				}
			case Perlin:
				return SinglePerlin(m_seed, x, y, z);
			case PerlinFractal:
				switch (m_fractalType) {
					case FBM:
						return SinglePerlinFractalFBM(x, y, z);
					case Billow:
						return SinglePerlinFractalBillow(x, y, z);
					case RigidMulti:
						return SinglePerlinFractalRigidMulti(x, y, z);
					default:
						return 0;
				}
			case WhiteNoise:
				return GetWhiteNoise(x, y, z);
			default:
				return 0;
		}
	}

	public float GetNoise(float x, float y) {
		x *= m_frequency;
		y *= m_frequency;

		switch (m_noiseType) {
			case Value:
				return SingleValue(m_seed, x, y);
			case Perlin:
				return SinglePerlin(m_seed, x, y);
			case PerlinFractal:
				switch (m_fractalType) {
					case FBM:
						return SinglePerlinFractalFBM(x, y);
					case Billow:
						return SinglePerlinFractalBillow(x, y);
					case RigidMulti:
						return SinglePerlinFractalRigidMulti(x, y);
					default:
						return 0;
				}
			case WhiteNoise:
				return GetWhiteNoise(x, y);
			default:
				return 0;
		}
	}

	// White Noise

	private int FloatCast2Int(float f) {
		int i = Float.floatToRawIntBits(f);

		return i ^ (i >> 16);
	}

	public float GetWhiteNoise(float x, float y, float z, float w) {
		int xi = FloatCast2Int(x);
		int yi = FloatCast2Int(y);
		int zi = FloatCast2Int(z);
		int wi = FloatCast2Int(w);

		return ValCoord4D(m_seed, xi, yi, zi, wi);
	}

	public float GetWhiteNoise(float x, float y, float z) {
		int xi = FloatCast2Int(x);
		int yi = FloatCast2Int(y);
		int zi = FloatCast2Int(z);

		return ValCoord3D(m_seed, xi, yi, zi);
	}

	public float GetWhiteNoise(float x, float y) {
		int xi = FloatCast2Int(x);
		int yi = FloatCast2Int(y);

		return ValCoord2D(m_seed, xi, yi);
	}

	public float GetWhiteNoiseInt(int x, int y, int z, int w) {
		return ValCoord4D(m_seed, x, y, z, w);
	}

	public float GetWhiteNoiseInt(int x, int y, int z) {
		return ValCoord3D(m_seed, x, y, z);
	}

	public float GetWhiteNoiseInt(int x, int y) {
		return ValCoord2D(m_seed, x, y);
	}

	// Value Noise
	public float GetValueFractal(float x, float y, float z) {
		x *= m_frequency;
		y *= m_frequency;
		z *= m_frequency;

		switch (m_fractalType) {
			case FBM:
				return SingleValueFractalFBM(x, y, z);
			case Billow:
				return SingleValueFractalBillow(x, y, z);
			case RigidMulti:
				return SingleValueFractalRigidMulti(x, y, z);
			default:
				return 0;
		}
	}

	private float SingleValueFractalFBM(float x, float y, float z) {
		int seed = m_seed;
		float sum = SingleValue(seed, x, y, z);
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;
			z *= m_lacunarity;

			amp *= m_gain;
			sum += SingleValue(++seed, x, y, z) * amp;
		}

		return sum * m_fractalBounding;
	}

	private float SingleValueFractalBillow(float x, float y, float z) {
		int seed = m_seed;
		float sum = Math.abs(SingleValue(seed, x, y, z)) * 2 - 1;
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;
			z *= m_lacunarity;

			amp *= m_gain;
			sum += (Math.abs(SingleValue(++seed, x, y, z)) * 2 - 1) * amp;
		}

		return sum * m_fractalBounding;
	}

	private float SingleValueFractalRigidMulti(float x, float y, float z) {
		int seed = m_seed;
		float sum = 1 - Math.abs(SingleValue(seed, x, y, z));
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;
			z *= m_lacunarity;

			amp *= m_gain;
			sum -= (1 - Math.abs(SingleValue(++seed, x, y, z))) * amp;
		}

		return sum;
	}

	public float GetValue(float x, float y, float z) {
		return SingleValue(m_seed, x * m_frequency, y * m_frequency, z * m_frequency);
	}

	private float SingleValue(int seed, float x, float y, float z) {
		int x0 = FastFloor(x);
		int y0 = FastFloor(y);
		int z0 = FastFloor(z);
		int x1 = x0 + 1;
		int y1 = y0 + 1;
		int z1 = z0 + 1;

		float xs, ys, zs;
		switch (m_interp) {
			default:
			case Linear:
				xs = x - x0;
				ys = y - y0;
				zs = z - z0;
				break;
			case Hermite:
				xs = InterpHermiteFunc(x - x0);
				ys = InterpHermiteFunc(y - y0);
				zs = InterpHermiteFunc(z - z0);
				break;
			case Quintic:
				xs = InterpQuinticFunc(x - x0);
				ys = InterpQuinticFunc(y - y0);
				zs = InterpQuinticFunc(z - z0);
				break;
		}

		float xf00 = Lerp(ValCoord3D(seed, x0, y0, z0), ValCoord3D(seed, x1, y0, z0), xs);
		float xf10 = Lerp(ValCoord3D(seed, x0, y1, z0), ValCoord3D(seed, x1, y1, z0), xs);
		float xf01 = Lerp(ValCoord3D(seed, x0, y0, z1), ValCoord3D(seed, x1, y0, z1), xs);
		float xf11 = Lerp(ValCoord3D(seed, x0, y1, z1), ValCoord3D(seed, x1, y1, z1), xs);

		float yf0 = Lerp(xf00, xf10, ys);
		float yf1 = Lerp(xf01, xf11, ys);

		return Lerp(yf0, yf1, zs);
	}

	public float GetValue(float x, float y) {
		return SingleValue(m_seed, x * m_frequency, y * m_frequency);
	}

	private float SingleValue(int seed, float x, float y) {
		int x0 = FastFloor(x);
		int y0 = FastFloor(y);
		int x1 = x0 + 1;
		int y1 = y0 + 1;

		float xs, ys;
		switch (m_interp) {
			default:
			case Linear:
				xs = x - x0;
				ys = y - y0;
				break;
			case Hermite:
				xs = InterpHermiteFunc(x - x0);
				ys = InterpHermiteFunc(y - y0);
				break;
			case Quintic:
				xs = InterpQuinticFunc(x - x0);
				ys = InterpQuinticFunc(y - y0);
				break;
		}

		float xf0 = Lerp(ValCoord2D(seed, x0, y0), ValCoord2D(seed, x1, y0), xs);
		float xf1 = Lerp(ValCoord2D(seed, x0, y1), ValCoord2D(seed, x1, y1), xs);

		return Lerp(xf0, xf1, ys);
	}

	// Gradient Noise
	public float GetPerlinFractal(float x, float y, float z) {
		x *= m_frequency;
		y *= m_frequency;
		z *= m_frequency;

		switch (m_fractalType) {
			case FBM:
				return SinglePerlinFractalFBM(x, y, z);
			case Billow:
				return SinglePerlinFractalBillow(x, y, z);
			case RigidMulti:
				return SinglePerlinFractalRigidMulti(x, y, z);
			default:
				return 0;
		}
	}

	private float SinglePerlinFractalFBM(float x, float y, float z) {
		int seed = m_seed;
		float sum = SinglePerlin(seed, x, y, z);
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;
			z *= m_lacunarity;

			amp *= m_gain;
			sum += SinglePerlin(++seed, x, y, z) * amp;
		}

		return sum * m_fractalBounding;
	}

	private float SinglePerlinFractalBillow(float x, float y, float z) {
		int seed = m_seed;
		float sum = Math.abs(SinglePerlin(seed, x, y, z)) * 2 - 1;
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;
			z *= m_lacunarity;

			amp *= m_gain;
			sum += (Math.abs(SinglePerlin(++seed, x, y, z)) * 2 - 1) * amp;
		}

		return sum * m_fractalBounding;
	}

	private float SinglePerlinFractalRigidMulti(float x, float y, float z) {
		int seed = m_seed;
		float sum = 1 - Math.abs(SinglePerlin(seed, x, y, z));
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;
			z *= m_lacunarity;

			amp *= m_gain;
			sum -= (1 - Math.abs(SinglePerlin(++seed, x, y, z))) * amp;
		}

		return sum;
	}

	public float GetPerlin(float x, float y, float z) {
		return SinglePerlin(m_seed, x * m_frequency, y * m_frequency, z * m_frequency);
	}

	private float SinglePerlin(int seed, float x, float y, float z) {
		int x0 = FastFloor(x);
		int y0 = FastFloor(y);
		int z0 = FastFloor(z);
		int x1 = x0 + 1;
		int y1 = y0 + 1;
		int z1 = z0 + 1;

		float xs, ys, zs;
		switch (m_interp) {
			default:
			case Linear:
				xs = x - x0;
				ys = y - y0;
				zs = z - z0;
				break;
			case Hermite:
				xs = InterpHermiteFunc(x - x0);
				ys = InterpHermiteFunc(y - y0);
				zs = InterpHermiteFunc(z - z0);
				break;
			case Quintic:
				xs = InterpQuinticFunc(x - x0);
				ys = InterpQuinticFunc(y - y0);
				zs = InterpQuinticFunc(z - z0);
				break;
		}

		float xd0 = x - x0;
		float yd0 = y - y0;
		float zd0 = z - z0;
		float xd1 = xd0 - 1;
		float yd1 = yd0 - 1;
		float zd1 = zd0 - 1;

		float xf00 = Lerp(GradCoord3D(seed, x0, y0, z0, xd0, yd0, zd0), GradCoord3D(seed, x1, y0, z0, xd1, yd0, zd0), xs);
		float xf10 = Lerp(GradCoord3D(seed, x0, y1, z0, xd0, yd1, zd0), GradCoord3D(seed, x1, y1, z0, xd1, yd1, zd0), xs);
		float xf01 = Lerp(GradCoord3D(seed, x0, y0, z1, xd0, yd0, zd1), GradCoord3D(seed, x1, y0, z1, xd1, yd0, zd1), xs);
		float xf11 = Lerp(GradCoord3D(seed, x0, y1, z1, xd0, yd1, zd1), GradCoord3D(seed, x1, y1, z1, xd1, yd1, zd1), xs);

		float yf0 = Lerp(xf00, xf10, ys);
		float yf1 = Lerp(xf01, xf11, ys);

		return Lerp(yf0, yf1, zs);
	}

	public float GetPerlinFractal(float x, float y) {
		x *= m_frequency;
		y *= m_frequency;

		switch (m_fractalType) {
			case FBM:
				return SinglePerlinFractalFBM(x, y);
			case Billow:
				return SinglePerlinFractalBillow(x, y);
			case RigidMulti:
				return SinglePerlinFractalRigidMulti(x, y);
			default:
				return 0;
		}
	}

	private float SinglePerlinFractalFBM(float x, float y) {
		int seed = m_seed;
		float sum = SinglePerlin(seed, x, y);
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;

			amp *= m_gain;
			sum += SinglePerlin(++seed, x, y) * amp;
		}

		return sum * m_fractalBounding;
	}

	private float SinglePerlinFractalBillow(float x, float y) {
		int seed = m_seed;
		float sum = Math.abs(SinglePerlin(seed, x, y)) * 2 - 1;
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;

			amp *= m_gain;
			sum += (Math.abs(SinglePerlin(++seed, x, y)) * 2 - 1) * amp;
		}

		return sum * m_fractalBounding;
	}

	private float SinglePerlinFractalRigidMulti(float x, float y) {
		int seed = m_seed;
		float sum = 1 - Math.abs(SinglePerlin(seed, x, y));
		float amp = 1;

		for (int i = 1; i < m_octaves; i++) {
			x *= m_lacunarity;
			y *= m_lacunarity;

			amp *= m_gain;
			sum -= (1 - Math.abs(SinglePerlin(++seed, x, y))) * amp;
		}

		return sum;
	}

	public float GetPerlin(float x, float y) {
		return SinglePerlin(m_seed, x * m_frequency, y * m_frequency);
	}

	private float SinglePerlin(int seed, float x, float y) {
		int x0 = FastFloor(x);
		int y0 = FastFloor(y);
		int x1 = x0 + 1;
		int y1 = y0 + 1;

		float xs, ys;
		switch (m_interp) {
			default:
			case Linear:
				xs = x - x0;
				ys = y - y0;
				break;
			case Hermite:
				xs = InterpHermiteFunc(x - x0);
				ys = InterpHermiteFunc(y - y0);
				break;
			case Quintic:
				xs = InterpQuinticFunc(x - x0);
				ys = InterpQuinticFunc(y - y0);
				break;
		}

		float xd0 = x - x0;
		float yd0 = y - y0;
		float xd1 = xd0 - 1;
		float yd1 = yd0 - 1;

		float xf0 = Lerp(GradCoord2D(seed, x0, y0, xd0, yd0), GradCoord2D(seed, x1, y0, xd1, yd0), xs);
		float xf1 = Lerp(GradCoord2D(seed, x0, y1, xd0, yd1), GradCoord2D(seed, x1, y1, xd1, yd1), xs);

		return Lerp(xf0, xf1, ys);
	}
}