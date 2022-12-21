public class SCM
	implements Runnable
{
	public static void main(final String[] args)
	{
		double epsilon=Double.parseDouble(args[0]);
		int threads=0;
		if(args.length>0)
			threads=Integer.parseInt(args[1]);
		new SCM(epsilon, .001D, 1200, threads).run();
	}

	public SCM(final double epsilon, final double radius, final int density, final int threads)
	{
		this.epsilon=epsilon;
		this.radius=radius;
		this.density=density;
		this.threads=threads;
		this.fn=String.format(java.util.Locale.US, "SCM-%1.3f.png", epsilon);
		this.treshold=(int)(15.D*Math.log(256.D)/radius);
	}

	public void run()
	{
		startTime=System.currentTimeMillis();
		final var stpe=threads>0?
				java.util.concurrent.Executors.newWorkStealingPool(threads):
				java.util.concurrent.Executors.newWorkStealingPool();
		final var bi=new java.awt.image.BufferedImage(density, density, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
		final var raster=bi.getRaster();
		for(int i=0; i<density; i++)
		{
			final int i_=i;
			stpe.submit(()->
				{
					for(int j=0;
						2*j<density
						|| 2*j==density && 2*i_<=density;
						j++)
					{
						final int j_=j;
						final double[] z=new double[]
							{
								2.D*Math.PI*i_/density-Math.PI,
								2.D*Math.PI*(density-j_)/density-Math.PI,
							};
						final double value=Math.scalb(iterate(z), 8);
						final double[] pixel=new double[] {value};
						raster.setPixel(i_, j_, pixel);
						if(density-i_>=0 && density-i_<density && density-j_>=0 && density-j_<density)
							raster.setPixel(density-i_, density-j_, pixel);
					}
					progress();
				});
		}
		stpe.submit(()->
			{
				for(int j=density/2+1; j<density; j++)
				{
					final int j_=j;
					final double[] z=new double[]
						{
							-Math.PI,
							2.D*Math.PI*(density-j_)/density-Math.PI,
						};
					final double value=Math.scalb(iterate(z), 8);
					final double[] pixel=new double[] {value};
					raster.setPixel(0, j_, pixel);
				}
				progress();
			});
		stpe.shutdown();
		try
		{
			stpe.awaitTermination(24, java.util.concurrent.TimeUnit.HOURS);
		}
		catch(final InterruptedException e)
		{
			System.err.println(e);
		}
		try
		{
			javax.imageio.ImageIO.write(bi, "png", new java.io.File(fn));
			final var duration=java.time.Duration.ofMillis(System.currentTimeMillis()-startTime);

			System.out.printf("\u001B[2K\rFile written: “%s”: %d bytes, %02d:%02d:%02d spent\n",
				fn,
				java.nio.file.Files.size(java.nio.file.Path.of(fn)),
				duration.toHoursPart(),
				duration.toMinutesPart(),
				duration.toSecondsPart()
				);
		}
		catch(final java.io.IOException e)
		{
			System.err.println(e);
		}
	}

	private static double modulo(double x, final double mod)
	{
		while(x<0.D)
			x+=mod;
		return x % mod;
	}

	private static double normalize(final double x)
	{
		return modulo(x+Math.PI, 2.D*Math.PI)-Math.PI;
	}

	private double[] map(final double[] z)
	{
		double x=z[0], y=z[1];
		y+=(epsilon*Math.sin(x));
		y=normalize(y);
		x+=y;
		x=normalize(x);
		return new double[] {x, y};
	}

	private double dist(final double[] a, final double[] b)
	{
		double dx=Math.abs(a[0]-b[0]);
		double dy=Math.abs(a[1]-b[1]);
		return Math.hypot(Math.min(dx, 2.D*Math.PI-dx), Math.min(dy, 2.D*Math.PI-dy));
	}

	private double iterate(final double[] z)
	{
		double[] t=java.util.Arrays.copyOf(z, 2);
		double[] h=java.util.Arrays.copyOf(z, 2);

		int n=0;
		while(true)
		{
			t=map(t);
			h=map(map(h));
			if(dist(t, h)<radius)
				return Math.exp(-n*radius/15.D);
			if(n>treshold)
				return 0.D;
			n++;
		}
	}

	private void progress()
	{
		progress++;
		final long currentTime=System.currentTimeMillis();
		final long timeElapsed=currentTime-startTime;
		final long timeRemained=progress==0?
			Long.MAX_VALUE:
			(long)(timeElapsed*((double)(density)/(double)(progress)-1.D));
		final var eta=java.time.Duration.ofMillis(timeRemained);
		System.out.printf("\u001B[2K\r%s: %.2f%%\tETA: %02d:%02d:%02d %s",
			fn,
			100.D*progress/density,
			eta.toHoursPart(),
			eta.toMinutesPart(),
			eta.toSecondsPart(),
			"|/-\\".charAt(progress%4)
			);
		System.out.flush();
	}

	private int density=1200;
	private double radius=0.01D;
	private double epsilon=1.D;
	private int progress=0;
	private final String fn;
	private long startTime;
	private int treshold;
	private int threads;
}
