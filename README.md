# The Standard Chirikov Map Visualization

---

## General Information

The [standard Chirikov map](https://en.wikipedia.org/wiki/Standard_map) is a
symplectic transformation of the phase plane given by formula

	(x, y) ↦ (x+y+ε⋅sin(x), y+ε⋅sin(x)),

where ε is a small parameter. This mappping is correctly restricted to a torus
when x, y ∈ [-π; π) are taken modulo 2π.

For ε=0 the map is linear and only periodic and quasiperiodic orbits are
possible and the map is integrable. With a non-zero value of ε, some of the
orbits exhibit chaotic behavior.

As ε increases, the measure of the set of points with chaotic orbits increases.

Video visualizes this process. Each frame corresponds to the next increasing
value of ε. Each pixel of the frame is shaded according to the chaoticity of
the corresponding point’s orbit.

The chaoticity of the orbit is calculated as follows. Let z be the starting
point of the orbit, T be the Chirikov map, n be the smallest natural number
such that

	distance(T²ⁿ(z), Tⁿ(z))<ρ

for a small positive ρ. The larger n, the darker the pixel. This method is
inspired by the [Floyd’s cycle detection
algorithm](https://en.wikipedia.org/wiki/Floyd%27s_cycle-finding_algorithm).

---

## Building Video

The building of the video is a lengthy process. It took us several months
intermittently on CPU Intel Core i9-9900K.

The UNIX-like operating system is required. Prerequirements:

* java ≥ 11
* make
* ffmpeg

Simply run
```
make
```

The build can be interrupted and restarted at any time.

If the building is overheating your CPU, you can limit the number of threads
used:
```
make THREADS=4
```

Full list of variables you may want to tweak:

Description | Variable | Default value
----------- | -------- | -------------
The number of threads | `THREADS` | `0` (thread pool uses all available processors)
The video frame rate| `FRAMERATE` | `30`
Initial value of ε | `INITIAL` | `0`
Final value of ε | `FINAL` | `6`
Increment of ε | `STEP` | `.001`

## Copyright and License

© Anton Shvetz, 2022

This project is licensed under the
[CC-BY-SA-4.0 License](https://creativecommons.org/licenses/by-sa/4.0/deed).

