package gameserver.skill.effect;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.EmotionType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_RESURRECT;
import gameserver.services.TeleportService;
import gameserver.skill.model.Effect;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;


/**
 * @author kecimis
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectPositionalEffect")
public class ResurrectPositionalEffect extends EffectTemplate
{
    @Override
    public void applyEffect(final Effect effect)
    {
        final Player player = (Player)effect.getEffected();
        
        TeleportService.teleportTo(player, effect.getEffector().getWorldId(), effect.getEffector().getInstanceId(), effect.getEffector().getX(), effect.getEffector().getY(), effect.getEffector().getZ(), effect.getEffector().getHeading(), 0);
        
        //add task to player
        Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable(){
            @Override
            public void run()
            {
                //blank
            }
        }, 5 * 60 * 1000);//5minutes
        player.getController().addTask(TaskId.SKILL_RESURRECT, task);

        ThreadPoolManager.getInstance().schedule(new Runnable(){        
            @Override
            public void run()
            {                
                // SM_DIE Packet
                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, 0), true);
                int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
                boolean hasSelfRezItem = player.getReviveController().checkForSelfRezItem(player) && player.getController().getCanAutoRevive();
                PacketSendUtility.sendPacket(player, new SM_DIE(false, hasSelfRezItem, kiskTimeRemaining));
                player.getController().stopProtectionActiveTask();
                PacketSendUtility.sendPacket(player, new SM_RESURRECT(effect.getEffector(), effect.getSkillId()));
            }
        }, 800);

    }

    @Override
    public void calculate(Effect effect)
    {
        if(effect.getEffected() instanceof Player && effect.getEffected().getLifeStats().isAlreadyDead())
            super.calculate(effect);
    }
}
            
