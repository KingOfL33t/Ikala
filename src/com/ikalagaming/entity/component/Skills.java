package com.ikalagaming.entity.component;

/**
 * A list of abilities that an entity has. These act as limitations on what the
 * entity can and cannot do, as well as track how proficient they are at doing
 * any particular thing.
 *
 * @author Ches Burks
 *
 */
public class Skills extends Component {

	/**
	 * The name of the component returned by {@link #getType()}. ( {@value} )
	 */
	public static final String TYPE_NAME = "Skills";

	/**
	 * Returns {@link #TYPE_NAME}.
	 */
	@Override
	public String getType() {
		return TYPE_NAME;
	}
}
