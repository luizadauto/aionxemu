package gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import gameserver.skill.model.Effect;
import gameserver.utils.PacketSendUtility;


/**
 * @author kecimis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleRootEffect")
public class SimpleRootEffect extends EffectTemplate
{
    @Override
    public void applyEffect(Effect effect)
    {
        effect.addToEffectedController();
    }

    @Override
    public void calculate(Effect effect)
    {
        super.calculate(effect, StatEnum.ROOT_RESISTANCE, null);
    }

    @Override
    public void startEffect(final Effect effect)
    {
        final Creature effected = effect.getEffected();
        effect.setAbnormal(EffectId.KNOCKBACK.getEffectId());
        effected.getEffectController().setAbnormal(EffectId.KNOCKBACK.getEffectId());
        PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
    }

    @Override
    public void endEffect(Effect effect)
    {
        effect.getEffected().getEffectController().unsetAbnormal(EffectId.KNOCKBACK.getEffectId());
    }
    
}
