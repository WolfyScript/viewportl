/*
 * This JavaScript file was created to give the first documentation about the scripting of Particle Effects.
 * Any changes in this file will be overwritten on the next server start-up!
 * More documentation and features will be added in the future.
 * This system is still experimental and might change!
 *
 */


/*
player - the player the effect is spawned on.
slot - the equipment slot the effect is active (HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET)
location - the location of the player
particleData - the current data of the particle
tick - the current tick of the particle. The tick is being increased each in-game tick and can reach a maximum of the duration of the effect.
        e.g. Particle has a duration of 20 ticks. then the tick will count up to 20 starting at 0
*/
function onLocation(location, particleData, tick){

}

/*
block - the block the effect is spawned on.
location - the location of the block
particleData - the current data of the particle
tick - the current tick of the particle. The tick is being increased each in-game tick and can reach a maximum of the duration of the effect.
        e.g. Particle has a duration of 20 ticks. then the tick will count up to 20 starting at 0
*/
function onBlock(block, location, particleData, tick) {
  tick = tick*0.2;
  particleData.setRelativeY(0.5+tick*0.3);
  particleData.setRelativeX(0.5+Math.sin(tick)*0.2);
  particleData.setRelativeZ(0.5+Math.cos(tick)*0.2);
}

/*
player - the player the effect is spawned on.
slot - the equipment slot the effect is active (HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET)
location - the location of the player
particleData - the current data of the particle
tick - the current tick of the particle. The tick is being increased each in-game tick and can reach a maximum of the duration of the effect.
        e.g. Particle has a duration of 20 ticks. then the tick will count up to 20 starting at 0
*/
function onPlayer(player, slot, location, particleData, tick){
  tick = tick*0.5;
  if (slot.toString() == "HEAD") {
    particleData.setRelativeX(Math.sin(tick)*0.7);
    particleData.setRelativeY(2- tick*0.2);
    particleData.setRelativeZ(Math.cos(tick)*0.7);
  }
}
