package gameserver.model.gameobjects;

import gameserver.ai.npcai.TotemAi;
import gameserver.controllers.NpcController;
import gameserver.controllers.NpcWithCreatorController;
import gameserver.model.templates.VisibleObjectTemplate;
import gameserver.model.templates.spawn.SpawnTemplate;

/**
 * @author kecimis
 *
 */
public class Totem extends NpcWithCreator
{
    /**
     * 
     * @param objId
     * @param controller
     * @param spawnTemplate
     * @param objectTemplate
     */
    public Totem(int objId, NpcController controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate)
    {
        super(objId, controller, spawnTemplate, objectTemplate);
    }
    
    @Override
    public NpcWithCreatorController getController()
    {
        return (NpcWithCreatorController) super.getController();
    }
    public Totem getOwner()
    {
        return (Totem)this;
    }
    @Override
    public void initializeAi()
    {
        this.ai = new TotemAi();
        ai.setOwner(this);
    }
    
    /**
     * @return NpcObjectType.TOTEM
     */
    @Override
    public NpcObjectType getNpcObjectType()
    {
        return NpcObjectType.TOTEM;
    }
}
