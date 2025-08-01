package io.xeros.model.collisionmap;

import com.google.common.collect.Maps;
import io.xeros.Server;
import io.xeros.objects.ObjectAction;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public final class ObjectDef {

	private int opcode61;
	private boolean boolean3;
	public static Map<Integer, ObjectDef> LOADED = Maps.newConcurrentMap();

	public ObjectAction[] defaultActions;

	public static ObjectDef getObjectDef(int id) {
		if (id > streamIndices.length) {
			//System.out.println("Object id out of range: " + id);
			id = streamIndices.length - 1;
		}

		for (int j = 0; j < 20; j++) {
			if (cache[j].type == id) {
				return cache[j];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDef class46 = cache[cacheIndex];

		if (id > streamIndices.length - 1 || id < 0) {
			return null;
		}

		stream.currentOffset = streamIndices[id];

		class46.type = id;
		class46.setDefaults();
		class46.readValues(stream);
		LOADED.put(id, class46);

		return class46;
	}

	public static void forEach(Consumer<ObjectDef> consumer) {
		for (ObjectDef def : LOADED.values()) {
			if (def != null)
				consumer.accept(def);
		}
	}

	public int getOption(String... searchOptions){
		if (actions != null) {
			for (String s : searchOptions) {
				for (int i = 0; i < actions.length; i++) {
					String option = actions[i];
					if (s.equalsIgnoreCase(option)) {
						return i+1;
					}
				}
			}
		}
		return -1;
	}

	public static ObjectDef get(int id) {
		return LOADED.get(id);
	}

	private void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		xLength = 1;
		yLength = 1;
		aBoolean767 = true;
		aBoolean757 = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean769 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	static ByteStreamExt stream;

	private static int[] streamIndices;

	public static void loadConfig() throws IOException {
		stream = new ByteStreamExt(getBuffer("loc.dat"));
		ByteStreamExt stream = new ByteStreamExt(getBuffer("loc.idx"));
		int objects = stream.readUnsignedWord();
		System.out.println("Total objects: " + objects);
		totalObjects = objects;
		streamIndices = new int[objects];
		int i = 2;
		for (int j = 0; j < objects; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}
		cache = new ObjectDef[20];
		for (int k = 0; k < 20; k++) {
			cache[k] = new ObjectDef();
		}
		System.out.println(objects + " Object definitions loaded.");
	}

	public static byte[] getBuffer(String s) {
		try {
			java.io.File f = new java.io.File(Server.getDataDirectory() + "/mapdata/" + s);
			if (!f.exists())
				return null;
			byte[] buffer = new byte[(int) f.length()];
			try (java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f))) {
				dis.readFully(buffer);
				dis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return buffer;
		} catch (Exception e) {
		}
		return null;
	}

	int int1 = -1;

	private void readValues(ByteStreamExt buffer) {
		int i = -1;
		label0: do {
			int opcode;

			opcode = buffer.readUnsignedByte();
			if (opcode == 0)
				break label0;
			if (opcode == 1) {
				int modelLength = buffer.readUnsignedByte();
				if (modelLength > 0) {
					anIntArray773 = new int[modelLength];
					anIntArray776 = new int[modelLength];
					for (int i2 = 0; i2 < modelLength; i2++) {
						anIntArray773[i2] = buffer.readUnsignedWord();
						anIntArray776[i2] = buffer.readUnsignedByte();
					}
				} else {
					stream.currentOffset += modelLength * 3;
				}
			} else if (opcode == 2)
				name = buffer.readString();
			/*else if (opcode == 3)
				description = buffer.readString();*/
			else if (opcode == 5) {
				int length = buffer.readUnsignedByte();
				if (length > 0) {
					anIntArray776 = null;
					anIntArray773 = new int[length];
					for (int l1 = 0; l1 < length; l1++)
						anIntArray773[l1] = buffer.readUnsignedWord();
				} else {
					stream.currentOffset += length * 2;
				}
			} else if (opcode == 14)
				xLength = buffer.readUnsignedByte();
			else if (opcode == 15)
				yLength = buffer.readUnsignedByte();
			else if (opcode == 17) {
				aBoolean767 = false;
				aBoolean757 = false;
			}
			else if (opcode == 18)
				aBoolean757 = false;
			else if (opcode == 19) {
				int1 = buffer.readUnsignedByte();
				if (int1 == 1)
					hasActions = true;
			} else if (opcode == 21)
				aBoolean762 = true;
			else if (opcode == 22)
				aBoolean769 = true;
			else if (opcode == 23)
				aBoolean764 = true;
			else if (opcode == 24) {
				anInt781 = buffer.readUnsignedWord();
				if (anInt781 == 65535)
					anInt781 = -1;
			} else if (opcode == 28)
				anInt775 = buffer.readUnsignedByte();
			else if (opcode == 29)
				aByte737 = buffer.readSignedByte();
			else if (opcode == 39)
				aByte742 = buffer.readSignedByte();
			else if (opcode >= 30 && opcode < 39) {
				if (actions == null)
					actions = new String[10];
				actions[opcode - 30] = buffer.readString();
				if (actions[opcode - 30].equalsIgnoreCase("hidden"))
					actions[opcode - 30] = null;
			} else if (opcode == 40) {
				int i1 = buffer.readUnsignedByte();
				originalModelColors = new int[i1];
				modifiedModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = buffer.readUnsignedWord();
					originalModelColors[i2] = buffer.readUnsignedWord();
				}
			} else if (opcode == 41) {
				int length = buffer.readUnsignedByte();
				// originalTexture = new short[length];
				// modifiedTexture = new short[length];
				for (int textureId = 0; textureId < length; textureId++) {
					/* originalTexture[textureId] = (short) */buffer.readUnsignedWord();
					/* modifiedTexture[textureId] = (short) */buffer.readUnsignedWord();
				}
			} else if (opcode == 61)
				opcode61 = buffer.readUnsignedWord();
			else if (opcode == 62)
				aBoolean751 = true;
			else if (opcode == 64)
				aBoolean779 = false;
			else if (opcode == 65)
				anInt748 = stream.readUnsignedWord();
			else if (opcode == 66)
				anInt772 = stream.readUnsignedWord();
			else if (opcode == 67)
				anInt740 = stream.readUnsignedWord();
			else if (opcode == 68)
				anInt758 = buffer.readUnsignedWord();
			else if (opcode == 69)
				anInt768 = buffer.readUnsignedByte();
			else if (opcode == 70)
				anInt738 = buffer.readSignedWord();
			else if (opcode == 71)
				anInt745 = buffer.readSignedWord();
			else if (opcode == 72)
				anInt783 = buffer.readSignedWord();
			else if (opcode == 73)
				aBoolean736 = true;
			else if (opcode == 74) {
				aBoolean766 = true;
			} else if (opcode == 75) {
				stream.readUnsignedByte();
			} else if (opcode == 77 || opcode == 92) {
				anInt774 = buffer.readUnsignedWord();
				if (anInt774 == 65535)
					anInt774 = -1;
				anInt749 = buffer.readUnsignedWord();
				if (anInt749 == 65535)
					anInt749 = -1;
				int var3 = -1;
				if(opcode == 92) var3 = buffer.readUnsignedWord();
				int j1 = buffer.readUnsignedByte();
				childrenIDs = new int[j1 + 1];
				for (int j2 = 0; j2 <= j1; j2++) {
					childrenIDs[j2] = buffer.readUnsignedWord();
					if (childrenIDs[j2] == 65535)
						childrenIDs[j2] = -1;
				}
			} else if(opcode == 78) {//ambient sound
				buffer.readUnsignedWord();
				buffer.readUnsignedByte();
			} else if(opcode == 79) {
				buffer.currentOffset += 5;
				int len = stream.readUnsignedByte();
				buffer.currentOffset += len * 2;
			} else if(opcode == 81) {
				buffer.readUnsignedByte();
			} else if(opcode == 82) {
				anInt746 =buffer.readUnsignedWord();
			} else if (opcode == 89) {
				this.boolean3 = false;
			}
		} while (true);
        hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
        if (actions != null) hasActions = true;
        if (aBoolean766) {
			aBoolean767 = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1)
			anInt760 = aBoolean767 ? 1 : 0;
	}

	public boolean clippedDecoration() {
		return aBoolean736 || int1 != 0 || aBoolean767;
	}

	private ObjectDef() {
		type = -1;
	}

	public boolean hasActions() {
		return hasActions;
	}

	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	public boolean solid() {
		return aBoolean779;
	}

	public int xLength() {
		return xLength;
	}

	public int yLength() {
		return yLength;
	}

	public boolean aBoolean767() {
		return aBoolean767;
	}

	boolean aBoolean766;
	public boolean aBoolean736;
	public String name;
	public int xLength;
	public int anInt746;
	private int[] originalModelColors;
	public int anInt749;
	public static boolean lowMem;
	public int type;
	public boolean aBoolean757;
	public int anInt758;
	public int[] childrenIDs;
	public int yLength;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public boolean aBoolean767;
	public int walkType = 2;
	public int anInt768;
	private static int cacheIndex;
	private int[] anIntArray773;
	public int anInt774;
	public int anInt775;
	public int anInt738;
	public int anInt740; // Width
	public int anInt745;
	public int anInt783;
	private boolean field3621;
	public int anInt772; // Height
	private int[] anIntArray776;
	public int anInt760;
	public String description;
	public boolean hasActions;
	public boolean aBoolean779;
	public byte aByte737;
	public byte aByte742;
	public boolean aBoolean769;
	public int anInt748; // Thickness
	public boolean aBoolean751;
	public int anInt781;
	public static int totalObjects;
	private static ObjectDef[] cache;
	private int[] modifiedModelColors;
	public String[] actions;

}
