package com.ikalagaming.entity.component;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.entity.Entity;
import com.ikalagaming.entity.effects.Effect;
import com.ikalagaming.entity.events.EffectActivated;
import com.ikalagaming.entity.events.EffectDeactivated;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.EventManager;

/**
 * Allows the entity to have effects applied to it.
 *
 * @author Ches Burks
 *
 */
public class Effects extends Component {

	private Entity theOwner;

	/**
	 * creates a new effects object with a reference to the owner
	 * 
	 * @param owner the entity onto which effects are to be applied
	 */
	public Effects(Entity owner) {
		this.theOwner = owner;
		this.currentEffects = new HashSet<>();
	}

	/**
	 * The name of the component returned by {@link #getType()}. ( {@value} )
	 */
	public static final String TYPE_NAME = "Effects";

	private Set<Effect> currentEffects;

	/**
	 * Adds a specified effect to the owner
	 * 
	 * @param effect the effect to apply
	 */
	public void addEffect(Effect effect) {
		if (currentEffects.contains(effect)) {
			return;
		}
		currentEffects.add(effect);
		EffectActivated e = new EffectActivated(effect, theOwner);
		EventManager.getInstance().fireEvent(e);
	}

	/**
	 * Removes a specified effect from the owner if it is applied
	 * 
	 * @param effect the effect to remove
	 */
	public void removeEffect(Effect effect) {
		if (!currentEffects.contains(effect)) {
			return;
		}
		currentEffects.remove(effect);
		EffectDeactivated e = new EffectDeactivated(effect, theOwner);
		EventManager.getInstance().fireEvent(e);
	}

	/**
	 * When an effect is activated, apply it to the
	 * 
	 * @param event The effect that was activated
	 */
	@EventHandler
	public void onEffectActivated(EffectActivated event) {

	}

	/**
	 * Returns {@link #TYPE_NAME}.
	 */
	@Override
	public String getType() {
		return TYPE_NAME;
	}
}
