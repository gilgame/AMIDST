package amidst.auto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class SeedFinder
{
	private Thread _worker;
	
	private boolean _stopped = true;
	public boolean isRunning()
	{
		return (_worker == null) ? false : !_stopped;
	}

	private final List<IThreadListener> _listeners
		= Collections.synchronizedList(new ArrayList<IThreadListener>());
	public void addListener(IThreadListener listener)
	{
		_listeners.add(listener);
	}

	private final List<Biome> _required = new ArrayList<Biome>();
	public void addBiome(Biome biome)
	{
		if (!_required.contains(biome))
		{
			_required.add(biome);
		}
	}
	
	private long _seed;
	public long getSeed()
	{
		return _seed;
	}
	
	private String _type;
	public String getType()
	{
		return _type;
	}

	private int _tries = 0;
	public int getTries()
	{
		return _tries;
	}

	public void start(final String type)
	{
		if (!isRunning())
		{
			_stopped = false;
			_type = type;
			_worker = new Thread(new Runnable()
			{
				public void run()
				{
						task(type);
				}
			});
			_worker.start();

			fireStartedEvent();
		}
	}

	public void stop()
	{
		if (isRunning())
		{
			_stopped = true;

			fireStoppedEvent();
		}
	}

	private void fireStartedEvent()
	{
		for (IThreadListener listener : _listeners)
		{
			listener.started();
		}
	}

	private void fireStoppedEvent()
	{
		for (IThreadListener listener : _listeners)
		{
			listener.stopped();
		}
	}

	private void fireCompletedEvent()
	{
		for (IThreadListener listener : _listeners)
		{
			listener.completed();
		}
	}

	private void task(String type)
	{
		_tries = 0;

		List<Integer> required = getIndexList(_required);
		do
		{
			_tries++;
			_seed = getRandomSeed();
			
			MinecraftUtil.createWorld(_seed, type);
			
			List<Integer> found = new ArrayList<Integer>();

			int p = -384, d = p * -2, s = 128;
			for (int a = p; a < d/2; a += s)
			{
				for (int b = p; b < d/2; b += s)
				{
					int[] data = MinecraftUtil.getBiomeData(a, b, s, s);
					
					for (int i : data)
					{
						if (!found.contains(i))
						{
								found.add(i);
						}
					}
				}
			}

			if (found.containsAll(required))
			{
				_stopped = true;
				fireCompletedEvent();
			}
		}
		while (!_stopped);
	}

	private long getRandomSeed()
	{
		return new Random().nextLong();
	}

	private List<Integer> getIndexList(List<Biome> biomes)
	{
		List<Integer> indices = new ArrayList<Integer>();
		for (Biome biome : biomes)
		{
			indices.add(biome.index);
		}
		return indices;
	}
}
