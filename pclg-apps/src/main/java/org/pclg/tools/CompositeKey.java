package org.pclg.tools;

/**
* @author El coyote Cojo
* @since 29/01/16 20:41
*/
public final class CompositeKey<A, B> {
	private final A partA;
	private final B partB;

	public CompositeKey(final A partA, final B partB) {
		this.partA = partA;
		this.partB = partB;
	}

	public A getPartA() {
		return partA;
	}

	public B getPartB() {
		return partB;
	}

	@Override
	public int hashCode() {
		final int hashA = partA == null ? 0 : partA.hashCode();
		final int hashB = partB == null ? 0 : partB.hashCode();
		return hashA ^ hashB;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CompositeKey)) {
			return false;
		}
		final CompositeKey other = (CompositeKey) obj;
		if (partA == null) {
			if (other.partA != null) {
				return false;
			} else if (!partA.equals(other.partA)) {
				return false;
			}
		}

		if (partB == null) {
			if (other.partB != null) {
				return false;
			} else if (!partB.equals(other.partB)) {
				return false;
			}
		}
		return  true;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("{")
			.append(partA == null ? "null" : partA.toString()).append(", ")
			.append(partB == null ? "null" : partB.toString()).append("}")
			.toString();
	}
}
