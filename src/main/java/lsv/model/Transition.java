package lsv.model;

import java.util.Arrays;

/**
 * Each transition may have a set of actions to be performed. 
 * 
 **/
public class  Transition {
    private String source;
    private String target;
    private String [] actions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(actions, that.actions);

    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(actions);
        return result;
    }

    /**
     * Returns the source state of a transition.
     * @return the id of the source state
     * */
    public String getSource() {
	return source;
    }
    /**
     * Returns the target state of a transition.
     * @return the id of the target state
     * */
    public String getTarget() {
	return target;
    }
    /**
     * Returns the set of actions in a transition.
     * @return a set of actions.
     * */
    public String[] getActions() {
	return actions;
    }
	
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(this.source+"-");
	sb.append(Arrays.toString(this.actions)+"-");
	sb.append(this.target);
	return sb.toString();
    }
	
}
