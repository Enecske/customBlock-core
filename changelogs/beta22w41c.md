# Update _beta22w14c_

## Changes
- Implemented functionality of methods from `Block` and `AbstractBlock` classes:
  - `hasRandomTicks`
  - `randomTick`
  - `scheduledTick`
  - `precipitationTick`
  - `neighborUpdate`
  - `onBlockBreakStart`
  - `onBroken`
  - `onBreak`
  - `afterBreak`
  - `onDestroyedByExplosion`
  - `onSteppedOn`
  - `onLandedUpon`
  - `onEntityLand`
  - `onEntityCollision`
  - `onProjectileHit`
  - `shouldDropItemsOnExplosion`
  - `onStacksDropped`

## Known Bugs
- Experience handling was migrated from `onBreak` to `onStacksDropped`, but it doesn't work when player manually breaks the block, only when block was broken by player-ignited TNT