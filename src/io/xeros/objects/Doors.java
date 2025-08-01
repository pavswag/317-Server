package io.xeros.objects;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.world.objects.GlobalObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Doors {

    private static Doors singleton = null;

    private List<Doors> doors = new ArrayList<Doors>();

    private File doorFile;

    public static Doors getSingleton() {
        if (singleton == null) {
            singleton = new Doors("C:/Users/"+ Configuration.SAVES+"/Dropbox/etc/cfg/obj/doors.txt");
        }
        return singleton;
    }

    private Doors(String file) {
        doorFile = new File(file);
    }

    private Doors(int door, int x, int y, int z, int face, int type, int open) {
        this.doorId = door;
        this.originalId = door;
        this.doorX = x;
        this.doorY = y;
        this.originalX = x;
        this.originalY = y;
        this.doorZ = z;
        this.originalFace = face;
        this.currentFace = face;
        this.type = type;
        this.open = open;
    }

    private Doors getDoor(int id, int x, int y, int z) {
        for (Doors d : doors) {
            if (d.doorId == id) {
                if (d.doorX == x && d.doorY == y && d.doorZ == z) {
                    return d;
                }
            }
        }
        return null;
    }

    public boolean handleDoor(int id, int x, int y, int z) {

        Doors d = getDoor(id, x, y, z);

        if (d == null) {
            /*if (DoubleDoors.getSingleton().handleDoor(id, x, y, z)) {
                return true;
            }*/
            return false;
        }
        int xAdjustment = 0, yAdjustment = 0;
        if (d.type == 0) {
            if (d.open == 0) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    xAdjustment = -1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    yAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    xAdjustment = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    yAdjustment = -1;
                }
            } else if (d.open == 1) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    yAdjustment = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    xAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    yAdjustment = -1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    xAdjustment = -1;
                }
            }
        } else if (d.type == 9) {
            if (d.open == 0) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    xAdjustment = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    xAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    xAdjustment = -1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    xAdjustment = -1;
                }
            } else if (d.open == 1) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    xAdjustment = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    xAdjustment = 1;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    xAdjustment = -1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    xAdjustment = -1;
                }
            }
        }
        if (xAdjustment != 0 || yAdjustment != 0) {
            RegionProvider.getGlobal().get(d.doorX, d.doorY).setClipToZero(d.doorX, d.doorY, d.doorZ);
            Server.getGlobalObjects().add(new GlobalObject(-1, d.doorX, d.doorY, d.doorZ, d.originalFace, d.type, 0));
        }
        if (d.doorX == d.originalX && d.doorY == d.originalY) {
            d.doorX += xAdjustment;
            d.doorY += yAdjustment;
        } else {
            RegionProvider.getGlobal().get(d.doorX, d.doorY).setClipToZero(d.doorX, d.doorY, d.doorZ);
            Server.getGlobalObjects().add(new GlobalObject(-1, d.doorX, d.doorY, d.doorZ, 0, d.type, 0));
            d.doorX = d.originalX;
            d.doorY = d.originalY;
        }
        if(d.doorId != 11773 && d.doorId != 11772) {
            if (d.doorId == d.originalId) {
                if (d.open == 0) {
                    d.doorId += 1;
                } else if (d.open == 1) {
                    d.doorId -= 1;
                }
            } else if (d.doorId != d.originalId) {
                if (d.open == 0) {
                    d.doorId -= 1;
                } else if (d.open == 1) {
                    d.doorId += 1;
                }
            }
        } else {
            if (d.doorId == 11773) {
                d.doorId = 11772;
            } else if (d.doorId == 11772) {
                d.doorId = 11773;
            }
        }

        RegionProvider.getGlobal().get(d.doorX, d.doorY).setClipToZero(d.doorX, d.doorY, d.doorZ);
        Server.getGlobalObjects().add(new GlobalObject(d.doorId, d.doorX, d.doorY, d.doorZ, getNextFace(d), d.type, 0));
        return true;
    }

    private int getNextFace(Doors d) {
        int f = d.originalFace;
        if (d.type == 0) {
            if (d.open == 0) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 1;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 2;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 3;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 0;
                } else if (d.originalFace != d.currentFace){
                    f = d.originalFace;
                }
            } else if (d.open == 1) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 3;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 0;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 2;
                } else if (d.originalFace != d.currentFace){
                    f = d.originalFace;
                }
            }
        } else if (d.type == 9) {
            if (d.open == 0) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 3;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 2;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 0;
                } else if (d.originalFace != d.currentFace){
                    f = d.originalFace;
                }
            } else if (d.open == 1) {
                if (d.originalFace == 0 && d.currentFace == 0) {
                    f = 3;
                } else if (d.originalFace == 1 && d.currentFace == 1) {
                    f = 0;
                } else if (d.originalFace == 2 && d.currentFace == 2) {
                    f = 1;
                } else if (d.originalFace == 3 && d.currentFace == 3) {
                    f = 2;
                } else if (d.originalFace != d.currentFace){
                    f = d.originalFace;
                }
            }
        }
        d.currentFace = f;
        return f;
    }

    public void load() {
        long start = System.currentTimeMillis();
        try {
            singleton.processLineByLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Loaded "+ doors.size() +" doors in "+ (System.currentTimeMillis() - start) +"ms.");
    }

    private void processLineByLine() throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new FileReader(doorFile))) {
            while (scanner.hasNextLine()) {
                processLine(scanner.nextLine());
            }
        }
    }

    protected void processLine(String line){
        Scanner scanner = new Scanner(line);
        try (scanner) {
            scanner.useDelimiter(" ");
            while (scanner.hasNextLine()) {
                int id = Integer.parseInt(scanner.next());
                int x = Integer.parseInt(scanner.next());
                int y = Integer.parseInt(scanner.next());
                int face = Integer.parseInt(scanner.next());
                int z = Integer.parseInt(scanner.next());
                int type = Integer.parseInt(scanner.next());
                doors.add(new Doors(id, x, y, z, face, type, alreadyOpen(id) ? 1 : 0));
            }
        }
    }

    private boolean alreadyOpen(int id) {
        for (int i = 0; i < openDoors.length; i++) {
            if (openDoors[i] == id) {
                return true;
            }
        }
        return false;
    }

    private int doorId;
    private int originalId;
    private int doorX;
    private int doorY;
    private int originalX;
    private int originalY;
    private int doorZ;
    private int originalFace;
    private int currentFace;
    private int type;
    private int open;

    private static int[] openDoors = {
            1504, 1514, 1517, 1520, 1531,
            1534, 1536, 1541, 2033, 2035, 2037, 2998,
            3271, 4468, 4697, 6101,6103,
            6105, 6107, 6109, 6111, 6113,
            6115, 6976, 6978, 8696, 8819,
            10261, 10263,10265,11708,11710,
            11712,11715,11994,12445, 13002,
            24051, 24055, 37464, 23973
    };

}