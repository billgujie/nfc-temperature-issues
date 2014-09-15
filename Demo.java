package com.nxp.nfc_demo.widgets;

import java.io.IOException;
import com.nxp.nfc_demo.MainActivity;
import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.ntag.NTagI2C;
import android.nfc.FormatException;


/**
 * NFC temperature sensor demo
 * Using NXP NTAG I2C 2K chip
 * @author JieGu
 * 
 */
public class Demo {

	MainActivity main;
	NTagI2C tag;
	String answer;
	
	public enum Sector {
		Sector0((byte) 0x00), Sector1((byte) 0x01), Sector2((byte) 0x02), Sector3(
				(byte) 0x03);

		byte value;

		private Sector(byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}
	}


	public Demo(NTagI2C tag, MainActivity main) {
		try {
			this.main = main;
			this.tag = tag;
			
		} catch (Exception e) {
		}

	}


	/**
	 * Performs the Temperature Demo
	 * @throws SmartCardException 
	 */
	public void TEMP() throws IOException, FormatException, SmartCardException {
		byte[] Data = new byte[64];
		byte[] Byte_set;
		Byte_set = TEM.getOption().getBytes();
		Data[60] = Byte_set[0];
		write_SRAM_Block(Data);

		Byte_set = read_SRAM_Block();

		int Temp = 0;
		// Adding first "Byte"
		Temp = ((Byte_set[59] >> 5) & 0x00000007);
		// Adding second Byte
		Temp |= ((Byte_set[58] << 3) & 0x000007F8);

		TEM.setAnswer(Temp);

	}
	
	public void write_SRAM_Block(byte[] Data) throws SmartCardException, IOException{
		byte[] TxBuffer = new byte[4];
		int index = 0;
		
		//tag.enablePassModeDataDirection(true);
		
		//keep giving me error?!!
		tag.sectorSelect(Sector.Sector1.getValue());
		tag.enablePassMode(true);
		tag.enableSRamMirroring(true);
		tag.setSRamMirrorPage((int) 0xF0);
		

		for (int i = 0; i < 16; i++) {
			for (int d_i = 0; d_i < 4; d_i++) {
				if (index < Data.length)
					TxBuffer[d_i] = Data[index++];
				else
					TxBuffer[d_i] = (byte) 0x00;
			}
			tag.write((int)0xF0 + i, TxBuffer);
		}
	}
	
	@SuppressWarnings("unused")
	public byte[] read_SRAM_Block() throws IOException, SmartCardException {

		byte[] answer1, answer2;
		tag.enableSRamMirroring(true);
		tag.sectorSelect((byte) 0x00);
		answer1 = tag.fastRead(0xF0, 0xFF);
		answer2 = tag.readFixedSramMirrorData();

		return answer1;
	}

}
