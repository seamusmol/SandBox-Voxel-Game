package serverlevelgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jme3.math.Vector2f;

import Configs.ServerSettings;
import ServerRigidBodies.RigidBodyCompressionUtil;
import ServerRigidBodies.ServerRigidBody;
import Util.CompressionUtil;

public class ChunkRegionIOUtil {

	public static ChunkRegion importChunkRegion(WorldSettings WorldSettings, String MapName, int IDX, int IDZ) 
	{
		Queue<Byte> data = new ConcurrentLinkedQueue();
		
		try {
			File tempFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/regions/" + MapName +"/" + IDX + "-" + IDZ + ".cnk");
			InputStream inputStream = new FileInputStream(tempFile);
			
			int b = 0;
			while((b = inputStream.read())!= -1)
			{
				data.add((byte) b);
			}
			
			int regionSize = WorldSettings.regionSize;

			Queue[][] regionDataList = new ConcurrentLinkedQueue[regionSize][regionSize];

			int[][] datasizes = new int[regionSize][regionSize];

			for (int i = 0; i < regionSize; i++) 
			{
				for (int j = 0; j < regionSize; j++) 
				{
					datasizes[i][j] = data.poll() & 0xFF | (data.poll() & 0xFF) << 8 | (data.poll() & 0xFF) << 16 | (data.poll() & 0xFF) << 24;
				}
			}
			for (int i = 0; i < regionSize; i++) 
			{
				for (int j = 0; j < regionSize; j++) 
				{
					int dataLength = datasizes[i][j];

					if (dataLength <= 0) 
					{
						regionDataList[i][j] = new ConcurrentLinkedQueue<Byte>();
					}
					else {
						Queue<Byte> dataSegment = new ConcurrentLinkedQueue<Byte>();
						for (int k = 0; k < dataLength; k++) 
						{
							dataSegment.add(data.poll());
						}
						regionDataList[i][j] = dataSegment;
					}
				}
			}
			
			Queue<ServerRigidBody> rigidBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
			
//			//rigidbodies
			int rigidBodyCount = data.poll() & 0xFF | (data.poll() & 0xFF) << 8| (data.poll() & 0xFF) << 16 | (data.poll() & 0xFF) << 24;
			for(int i = 0; i < rigidBodyCount; i++)
			{
				int length = data.poll() & 0xFF | (data.poll() & 0xFF) << 8| (data.poll() & 0xFF) << 16 | (data.poll() & 0xFF) << 24;
				
				byte[] dataSegment = new byte[length];
				for (int j = 0; j < length; j++) 
				{
					dataSegment[j] = data.poll();
				}
				if(dataSegment.length < 56)
				{
					break;
				}
				
				rigidBodies.add( new ServerRigidBody(RigidBodyCompressionUtil.UnpackRigidBodyData(dataSegment, 0)));
			}
			
			inputStream.close();
			return new ChunkRegion(WorldSettings, MapName, IDX, IDZ, regionDataList, rigidBodies);

		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static boolean hasImport(String MapName, int IDX, int IDZ) 
	{
		File tempFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/regions/"+ MapName + "/" + IDX + "-" + IDZ + ".cnk");
		return tempFile.exists() && tempFile.canRead();
	}

}
