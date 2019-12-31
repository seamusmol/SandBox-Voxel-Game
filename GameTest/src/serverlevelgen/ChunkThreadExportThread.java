package serverlevelgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import ServerRigidBodies.ServerRigidBody;
import Util.CompressionUtil;

public class ChunkThreadExportThread implements Runnable {

    String mapName;
    ChunkRegion chunkRegion;
    
    
    public ChunkThreadExportThread(String MapName, ChunkRegion ChunkRegion) 
    {
    	mapName = MapName;
        chunkRegion = ChunkRegion;
    }


    @Override
    public void run() 
    {
        Thread.currentThread().setName("Util-ChunkRegionExport-" + chunkRegion.idx + "-" + chunkRegion.idz);
        
        File directory = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/regions/" + mapName + "/");
        if (!directory.exists()) 
        {
            directory.mkdir();
        }
        File exportFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/regions/" + mapName + "/" + chunkRegion.getIDX() + "-" + chunkRegion.getIDZ() + ".cnk");
        FileOutputStream writeStream = null;
        try {
            if (!exportFile.exists()) {
                exportFile.createNewFile();
                exportFile.setReadable(true);
                exportFile.setWritable(true);
            }
            writeStream = new FileOutputStream(exportFile, false);
            
            Queue<Byte>[][] chunks = chunkRegion.getRegionDataList();
            
            for (int i = 0; i < chunks.length; i++) {
                for (int j = 0; j < chunks[0].length; j++) {
                    if (chunks[i][j].isEmpty() || chunks[i][j] == null) {
                        byte[] datalength = CompressionUtil.getIntArray(0);
                        writeStream.write(datalength[3]);
                        writeStream.write(datalength[2]);
                        writeStream.write(datalength[1]);
                        writeStream.write(datalength[0]);
                    } else {
                        byte[] datalength = CompressionUtil.getIntArray(chunks[i][j].size());
                        writeStream.write(datalength[3]);
                        writeStream.write(datalength[2]);
                        writeStream.write(datalength[1]);
                        writeStream.write(datalength[0]);
                    }
                }
            }
            int size = 0;
            for (int i = 0; i < chunks.length; i++) 
            {
                for (int j = 0; j < chunks[0].length; j++) 
                {
                    Queue<Byte> chunkData = chunks[i][j];
                    if(chunkData != null)
                    {
                    	while(!chunkData.isEmpty())
	                    {
	                    	 writeStream.write(chunkData.poll());
	                    }
                    	size+= chunkData.size();
                    }
                }
            }
            
            Queue<ServerRigidBody> rigidBodyList = chunkRegion.getRigidBodies();
            byte[] rigidBodyCount = CompressionUtil.getIntArray(rigidBodyList.size());
            writeStream.write(rigidBodyCount[3]);
            writeStream.write(rigidBodyCount[2]);
            writeStream.write(rigidBodyCount[1]);
            writeStream.write(rigidBodyCount[0]);
            
            for(ServerRigidBody rigidBody : rigidBodyList)
            {
            	List<Byte> data = rigidBody.GetData();
            	
            	byte[] datalength = CompressionUtil.getIntArray(data.size());
                writeStream.write(datalength[3]);
                writeStream.write(datalength[2]);
                writeStream.write(datalength[1]);
                writeStream.write(datalength[0]);
                
                for(int j = 0; j < data.size(); j++)
            	{
            		writeStream.write(data.get(j));
            	} 
            }
            
            Queue<ServerRigidBody> inactiveRigidBodyList = chunkRegion.getInactiveBodies();
            byte[] inactiveRigidBodyCount = CompressionUtil.getIntArray(inactiveRigidBodyList.size());
            writeStream.write(inactiveRigidBodyCount[3]);
            writeStream.write(inactiveRigidBodyCount[2]);
            writeStream.write(inactiveRigidBodyCount[1]);
            writeStream.write(inactiveRigidBodyCount[0]);
            
            for(ServerRigidBody rigidBody : inactiveRigidBodyList)
            {
            	List<Byte> data = rigidBody.GetData();
            	
            	byte[] datalength = CompressionUtil.getIntArray(data.size());
                writeStream.write(datalength[3]);
                writeStream.write(datalength[2]);
                writeStream.write(datalength[1]);
                writeStream.write(datalength[0]);
                
                for(int j = 0; j < data.size(); j++)
            	{
            		writeStream.write(data.get(j));
            	} 
            }
            if(exportFile.length() != size)
	        {
            	exportFile.createNewFile();
	        }
            writeStream.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        chunkRegion.hasFinishedExport = true;
        chunkRegion.hasExport = false;
        
//        Thread.currentThread().stop();
    }
}
