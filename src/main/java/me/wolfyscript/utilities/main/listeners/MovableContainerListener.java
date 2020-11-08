package me.wolfyscript.utilities.main.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class MovableContainerListener {

    Server server;

    public MovableContainerListener(Server this_server) {
        this.server = this_server;
    }

    /*
     * EVERYTHING down below is EXPERIMENTAL!
     */

    public void moveChest(Chest oldChest, Location oldLoc, double x, double y, double z) {
        Inventory oldInv;
        Inventory newInv;
        DoubleChestInventory oldDblInv;
        DoubleChestInventory newDblInv;
        Chest newChest;
        Chest newOtherChest;
        BlockState newState;
        BlockState newOtherState;
        ItemStack[] oldItems;
        MaterialData oldMatData;
        MaterialData oldOtherMatData;
        Block otherChestBlock = this.GetAttachedChest(oldChest.getBlock());
        Chest oldOtherChest;

        //need to substract because it actually modifies the original Location object
        oldLoc = oldLoc.subtract(x, y, z);

        Location newLoc = oldLoc.add(x, y, z);
        Location newLoc2;
        Location oldLoc2;

        //test if we got a double chest
        if (otherChestBlock != null) {
            //got a double chest to move ! yay !

            //get the locations of the other chest block
            oldLoc2 = otherChestBlock.getLocation();
            newLoc2 = oldLoc2.add(x, y, z);

            //get the other chest block state
            oldOtherChest = (Chest) otherChestBlock.getState();

            //get a double chest inventory
            oldDblInv = (DoubleChestInventory) oldChest.getInventory();

            //store a copy of the items
            oldItems = oldDblInv.getContents().clone();

            //get a copy of the data of the old chest
            oldMatData = oldChest.getData();
            oldOtherMatData = oldOtherChest.getData();

            //clear the inventory of the old chest
            oldDblInv.clear();

            //replace the old chest block by AIR
            oldChest.setType(Material.AIR);
            oldOtherChest.setType(Material.AIR);

            //update the old block
            if (!(oldChest.update(true) && oldOtherChest.update(true))) {
                this.server.broadcastMessage("MovableChest: Could not update old DoubleChest block!");
            }

            //get the new block state
            newState = newLoc.getBlock().getState();
            newOtherState = newLoc2.getBlock().getState();

            //set the new block material
            newState.setType(Material.CHEST);
            newOtherState.setType(Material.CHEST);

            //update the new block state
            newState.update(true);
            newOtherState.update(true);

            //get the new chest
            newChest = (Chest) newState.getBlock().getState();
            newOtherChest = (Chest) newOtherState.getBlock().getState();

            //set the data of the new chest
            newChest.setData(oldMatData);
            newOtherChest.setData(oldOtherMatData);

            //get the inventory to the new chest
            newDblInv = (DoubleChestInventory) newChest.getInventory();

            //set the items of the new chest
            newDblInv.setContents(oldItems);

            //update the new block
            if (!(newChest.update(true) && (newOtherChest.update(true)))) {
                this.server.broadcastMessage("MovableChest: Could not update new Chest block!");
            }
        } else {
            //get the inventory of the chest
            oldInv = oldChest.getInventory();

            //store a copy of the items
            oldItems = oldInv.getContents().clone();

            //get a copy of the data of the old chest
            oldMatData = oldChest.getData();

            //clear the inventory of the old chest
            oldInv.clear();

            //replace the old chest block by AIR
            oldChest.setType(Material.AIR);

            //update the old block
            if (!oldChest.update(true)) {
                this.server.broadcastMessage("MovableChest: Could not update old Chest block!");
            }

            //get the new block state
            newState = newLoc.getBlock().getState();

            //set the new block material
            newState.setType(Material.CHEST);

            //update the new block state
            newState.update(true);

            //get the new chest
            newChest = (Chest) newState.getBlock().getState();

            //set the data of the new chest
            newChest.setData(oldMatData);

            //get the inventory to the new chest
            newInv = newChest.getInventory();

            //set the items of the new chest
            newInv.setContents(oldItems);

            //update the new block
            if (!newChest.update(true)) {
                this.server.broadcastMessage("MovableChest: Could not update new Chest block!");
            }
        }

    }










	/*
	@EventHandler
	public void onPiston( BlockPistonEvent event )
	{
		this.server.broadcastMessage( "BlockPistonEvent was fired!" );
	}
	*/


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPistonRetracted(BlockPistonRetractEvent event) {
        Block piston = null;
        BlockFace face = null;
        Block currentRelative = null;
        Block oldBlock = null;
        Block newBlock = null;
        boolean foundSomething = false;
        int currentRelativeIndex = 1;
        Location oldLoc = null;
        Location newLoc = null;
        Material oldMat = null;
        double x_offset = 0;
        double y_offset = 0;
        double z_offset = 0;
        BlockState oldState = null;
        BlockState newState = null;
        Furnace oldFurnaceState = null;
        Furnace newFurnaceState = null;
        BrewingStand oldBTState = null;
        BrewingStand newBTState = null;
        Dispenser oldDispenserState = null;
        Dispenser newDispenserState = null;
        Inventory oldInv = null;
        ItemStack[] oldItems = null;
        Inventory newInv = null;

        //this.server.broadcastMessage( "A piston was retracted!" );
        if (event.isSticky()) {
            piston = event.getBlock();
            face = event.getDirection();
            foundSomething = false;
            currentRelativeIndex = 2;

            currentRelative = piston.getRelative(face, currentRelativeIndex);
            if (currentRelative != null && !this.isNotAcceptedType(currentRelative.getType())) {
                //this.server.broadcastMessage( currentRelative.getType().toString() );
                if (currentRelative.getType().equals(Material.CHEST)
                        || currentRelative.getType().equals(Material.FURNACE)
                        || currentRelative.getType().equals(Material.DISPENSER)
                        || currentRelative.getType().equals(Material.BREWING_STAND)) {
                    foundSomething = true;
                }
            }

            if (foundSomething) {
                //this.server.broadcastMessage( "[retract] Chest detected in front of piston!" );
                x_offset = face.getModX();
                y_offset = face.getModY();
                z_offset = face.getModZ();

                //get the block
                oldBlock = piston.getRelative(face, currentRelativeIndex);

                //get the state of the old block
                oldState = oldBlock.getState();

                //get the location of the old block
                oldLoc = oldState.getLocation();

                //get the material of the old block
                oldMat = oldState.getType();

                //this.server.broadcastMessage( "Old block Material: " + oldMat.toString() );

                //get the new block location
                newLoc = oldLoc.add(x_offset, y_offset, z_offset);

                //get the new block
                newBlock = newLoc.getBlock();

                //get the state of the new block
                newState = newBlock.getState();

                //if the old block was a chest, copy the inventory to the new chest
                if (oldMat.equals(Material.CHEST)) {
                    this.moveChest((Chest) oldState, oldLoc, x_offset, y_offset, z_offset);

                } else if (oldMat.equals(Material.FURNACE)) {

                    //cast the old block state to a Chest
                    oldFurnaceState = (Furnace) oldState.getBlock().getState();

                    //get the inventory of the old chest
                    oldInv = oldFurnaceState.getInventory();

                    //store a copy of the items
                    oldItems = oldInv.getContents().clone();

                    oldBlock.getBlockData().clone();

                    //clear the inventory of the old chest
                    oldInv.clear();

                    //replace the old chest block by AIR
                    oldFurnaceState.setType(Material.AIR);

                    //update the old block
                    if (!oldFurnaceState.update(true)) {
                        this.server.broadcastMessage("MovableChest: Could not update old Furnace block!");
                    }

                    //set the new block material
                    newState.setType(Material.FURNACE);
                    newState.update(true);
                    newFurnaceState = (Furnace) newState.getBlock().getState();

                    //set the data of the new chest

                    //get the inventory to the new chest
                    newInv = newFurnaceState.getInventory();

                    //set the items of the new chest
                    newInv.setContents(oldItems);

                    //update the new block
                    if (!newFurnaceState.update(true)) {
                        this.server.broadcastMessage("MovableChest: Could not update new Furnace block!");
                    }
                } else if (oldMat.equals(Material.DISPENSER)) {

                    //cast the old block state to a Chest
                    oldDispenserState = (Dispenser) oldState.getBlock().getState();

                    //get the inventory of the old chest
                    oldInv = oldDispenserState.getInventory();

                    //store a copy of the items
                    oldItems = oldInv.getContents().clone();

                    //get a copy of the data of the old chest

                    //clear the inventory of the old chest
                    oldInv.clear();

                    //replace the old chest block by AIR
                    oldDispenserState.setType(Material.AIR);

                    //update the old block
                    if (!oldDispenserState.update(true)) {
                        this.server.broadcastMessage("MovableChest: Could not update old Dispenser block!");
                    }

                    //set the new block material
                    newState.setType(Material.DISPENSER);
                    newState.update(true);
                    newDispenserState = (Dispenser) newState.getBlock().getState();

                    //set the data of the new chest

                    //get the inventory to the new chest
                    newInv = newDispenserState.getInventory();

                    //set the items of the new chest
                    newInv.setContents(oldItems);

                    //update the new block
                    if (!newDispenserState.update(true)) {
                        this.server.broadcastMessage("MovableChest: Could not update new Dispenser block!");
                    }
                } else if (oldMat.equals(Material.BREWING_STAND)) {

                    //cast the old block state to a Chest
                    oldBTState = (BrewingStand) oldState.getBlock().getState();

                    //get the inventory of the old chest
                    oldInv = oldBTState.getInventory();

                    //store a copy of the items
                    oldItems = oldInv.getContents().clone();

                    //get a copy of the data of the old chest

                    //clear the inventory of the old chest
                    oldInv.clear();

                    //replace the old chest block by AIR
                    oldBTState.setType(Material.AIR);

                    //update the old block
                    if (!oldBTState.update(true)) {
                        this.server.broadcastMessage("MovableChest: Could not update old Brewing_Stand block!");
                    }

                    //set the new block material
                    newState.setType(Material.BREWING_STAND);
                    newState.update(true);
                    newBTState = (BrewingStand) newState.getBlock().getState();

                    //set the data of the new chest

                    //get the inventory to the new chest
                    newInv = newBTState.getInventory();

                    //set the items of the new chest
                    newInv.setContents(oldItems);

                    //update the new block
                    if (!newBTState.update(true)) {
                        this.server.broadcastMessage("MovableChest: Could not update new Brewing_Stand block!");
                    }
                }


            }

        }
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        List<Block> pistons = this.getTriggeredPistons(event);

        BlockFace face = null;
        Block currentRelative = null;
        Block oldBlock = null;
        Block newBlock = null;
        boolean foundSomething = false;
        int currentRelativeIndex = 1;
        int i = 1;
        Location oldLoc = null;
        Location newLoc = null;
        Material oldMat = null;
        double x_offset = 0;
        double y_offset = 0;
        double z_offset = 0;
        BlockState oldState = null;
        BlockState newState = null;
        Furnace oldFurnaceState = null;
        Furnace newFurnaceState = null;
        BrewingStand oldBTState = null;
        BrewingStand newBTState = null;
        Dispenser oldDispenserState = null;
        Dispenser newDispenserState = null;
        Inventory oldInv = null;
        ItemStack[] oldItems = null;
        Inventory newInv = null;

        for (Block piston : pistons) {
            //this.server.broadcastMessage( "Detected that a piston power state changed" );

            face = getPistonFace(piston.getData());

            foundSomething = false;
            currentRelativeIndex = 1;

            while (!foundSomething && currentRelativeIndex <= 12) {
                currentRelative = piston.getRelative(face, currentRelativeIndex);
                if (currentRelative != null && !this.isNotAcceptedType(currentRelative.getType())) {
                    //this.server.broadcastMessage( currentRelative.getType().toString() );
                    if (currentRelative.getType().equals(Material.CHEST)
                            || currentRelative.getType().equals(Material.FURNACE)
                            || currentRelative.getType().equals(Material.DISPENSER)
                            || currentRelative.getType().equals(Material.BREWING_STAND)) {
                        foundSomething = true;
                        break;
                    }
                    currentRelativeIndex++;
                } else {
                    //forces exit
                    break;
                }
            }

            if (foundSomething) {
                //this.server.broadcastMessage( "Chest detected in front of piston!" );


                //we have to move the chest 1 block away or towards the piston
                if (event.getNewCurrent() > 0) {
                    //move the chest 1 block away (and all the blocks in between)


                    //loop through the block list behind the piston
                    for (i = currentRelativeIndex; i >= 1; i--) {
                        //this.server.broadcastMessage( "New loop iteration | i = " + i );
                        //get the block
                        oldBlock = piston.getRelative(face, i);

                        //get the state of the old block
                        oldState = oldBlock.getState();

                        //get the location of the old block
                        oldLoc = oldState.getLocation();

                        //get the material of the old block
                        oldMat = oldState.getType();

                        //this.server.broadcastMessage( "Old block Material: " + oldMat.toString() );

                        x_offset = face.getModX();
                        y_offset = face.getModY();
                        z_offset = face.getModZ();

                        //get the new block location
                        newLoc = oldLoc.add(x_offset, y_offset, z_offset);

                        //get the new block
                        newBlock = newLoc.getBlock();

                        //get the state of the new block
                        newState = newBlock.getState();

                        //if the old block was a chest, copy the inventory to the new chest
                        if (oldMat.equals(Material.CHEST)) {

                            this.moveChest((Chest) oldState, oldLoc, x_offset, y_offset, z_offset);

                        } else if (oldMat.equals(Material.FURNACE)) {

                            //cast the old block state to a Chest
                            oldFurnaceState = (Furnace) oldState.getBlock().getState();

                            //get the inventory of the old chest
                            oldInv = oldFurnaceState.getInventory();

                            //store a copy of the items
                            oldItems = oldInv.getContents().clone();

                            //get a copy of the data of the old chest

                            //clear the inventory of the old chest
                            oldInv.clear();

                            //replace the old chest block by AIR
                            oldFurnaceState.setType(Material.AIR);

                            //update the old block
                            if (!oldFurnaceState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update old Furnace block!");
                            }

                            //set the new block material
                            newState.setType(Material.FURNACE);
                            newState.update(true);
                            newFurnaceState = (Furnace) newState.getBlock().getState();

                            //set the data of the new chest

                            //get the inventory to the new chest
                            newInv = newFurnaceState.getInventory();

                            //set the items of the new chest
                            newInv.setContents(oldItems);

                            //update the new block
                            if (!newFurnaceState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update new Furnace block!");
                            }
                        } else if (oldMat.equals(Material.DISPENSER)) {

                            //cast the old block state to a Chest
                            oldDispenserState = (Dispenser) oldState.getBlock().getState();

                            //get the inventory of the old chest
                            oldInv = oldDispenserState.getInventory();

                            //store a copy of the items
                            oldItems = oldInv.getContents().clone();

                            //get a copy of the data of the old chest

                            //clear the inventory of the old chest
                            oldInv.clear();

                            //replace the old chest block by AIR
                            oldDispenserState.setType(Material.AIR);

                            //update the old block
                            if (!oldDispenserState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update old Dispenser block!");
                            }

                            //set the new block material
                            newState.setType(Material.DISPENSER);
                            newState.update(true);
                            newDispenserState = (Dispenser) newState.getBlock().getState();

                            //set the data of the new chest

                            //get the inventory to the new chest
                            newInv = newDispenserState.getInventory();

                            //set the items of the new chest
                            newInv.setContents(oldItems);

                            //update the new block
                            if (!newDispenserState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update new Dispenser block!");
                            }
                        } else if (oldMat.equals(Material.BREWING_STAND)) {

                            //cast the old block state to a Chest
                            oldBTState = (BrewingStand) oldState.getBlock().getState();

                            //get the inventory of the old chest
                            oldInv = oldBTState.getInventory();

                            //store a copy of the items
                            oldItems = oldInv.getContents().clone();

                            //get a copy of the data of the old chest

                            //clear the inventory of the old chest
                            oldInv.clear();

                            //replace the old chest block by AIR
                            oldBTState.setType(Material.AIR);

                            //update the old block
                            if (!oldBTState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update old BrewingStand block!");
                            }

                            //set the new block material
                            newState.setType(Material.BREWING_STAND);
                            newState.update(true);
                            newBTState = (BrewingStand) newState.getBlock().getState();

                            //set the data of the new chest

                            //get the inventory to the new chest
                            newInv = newBTState.getInventory();

                            //set the items of the new chest
                            newInv.setContents(oldItems);

                            //update the new block
                            if (!newBTState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update new BrewingStand block!");
                            }
                        } else {
                            //not a chest: we just set the old block to AIR
                            oldState.setType(Material.AIR);

                            //update the old block
                            if (!oldState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update old block!");
                            }

                            //set the new block to the old block material
                            newState.setType(oldMat);

                            //update the old block
                            if (!newState.update(true)) {
                                this.server.broadcastMessage("MovableChest: Could not update new block!");
                            }
                        }
                    }
                }

            }
			/*else
			{
				//this.server.broadcastMessage( "No chest were detected in front of piston." );
			}*/
        }

    }


    /* returns the other block of a chest, or null if single chest */
    public Block GetAttachedChest(Block toinspect) {
        Block northblock = toinspect.getRelative(BlockFace.NORTH);
        if (northblock.getType() == Material.CHEST) {
            return northblock;
        }

        Block southblock = toinspect.getRelative(BlockFace.SOUTH);
        if (southblock.getType() == Material.CHEST) {
            return southblock;
        }

        Block eastblock = toinspect.getRelative(BlockFace.EAST);
        if (eastblock.getType() == Material.CHEST) {
            return eastblock;
        }

        Block westblock = toinspect.getRelative(BlockFace.WEST);
        if (westblock.getType() == Material.CHEST) {
            return westblock;
        }

        return null;
    }


    public List<Block> getTriggeredPistons(BlockRedstoneEvent event) {
        List<Block> result = new ArrayList<Block>();
        if ((event.getNewCurrent() > 0 && event.getOldCurrent() > 0)
                ||
                (event.getNewCurrent() == 0 && event.getOldCurrent() == 0)
        ) {
            //When power state didn't really change,
            return result;
        }
        //TODO: Implement all types of possible triggers
        BlockFace[] faces = {
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.WEST,
                BlockFace.UP,
                BlockFace.SELF,
                BlockFace.DOWN
        };
        for (BlockFace face : faces) {
            Block piston = event.getBlock().getRelative(face);

            if (piston.getType().equals(Material.PISTON)
                    ||
                    piston.getType().equals(Material.STICKY_PISTON)) {
                result.add(piston);
            }
        }
        return result;
    }

    public BlockFace getPistonFace(byte data) {
        BlockFace face = null;
        data = (byte) (data & 0x7);
        switch (data) {
            case 0:
                face = BlockFace.DOWN;
                break;
            case 1:
                face = BlockFace.UP;
                break;
            case 2:
                face = BlockFace.EAST;
                break;
            case 3:
                face = BlockFace.WEST;
                break;
            case 4:
                face = BlockFace.NORTH;
                break;
            case 5:
                face = BlockFace.SOUTH;
                break;
            default:
                face = null;
                break;
        }
        return face;
    }

    public boolean isNotAcceptedType(Material type) {
        switch (type) {
            case AIR:
            case CAVE_AIR:
            case VOID_AIR:
            case BEDROCK:
            case NOTE_BLOCK:
            case OBSIDIAN:
            case REDSTONE:
            case REDSTONE_TORCH:
            case REDSTONE_WALL_TORCH:
            case COMPARATOR:
            case COMMAND_BLOCK:
            case REPEATER:
            case REPEATING_COMMAND_BLOCK:
            case POPPY:
                return true;
        }
        return false;
    }

}
