package net.rptools.maptool.script.mt2api;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.campaign.TokenProperty;
import net.rptools.maptool.util.StringUtil;

/**
 * This class makes the TokenView a bad MapView on the token properties. The 
 * advantage of this that we can now access properties from groovy via beans
 * e.g.: token.HP="test" 
 * @author Virenerus
 *
 */
public abstract class TokenPropertyView implements Map<String, Object>{

	protected Token token;

	public TokenPropertyView(Token token) {
		this.token=token;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		if(key==null)
			return false;
		Object val = token.getProperty(key.toString());
		if (val == null) {
			return false;
		}

		if (StringUtil.isEmpty(val.toString())) {
			return false;
		}

		return true;
	}

	@Override
	public boolean containsValue(Object value) {
		for(String pn:token.getPropertyNames())
			if(token.getProperty(pn).equals(value))
				return true;
		return false;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key) {
		if(key==null)
			throw new NullPointerException();
		else
			return token.getProperty(key.toString());
	}

	@Override
	public boolean isEmpty() {
		return token.getPropertyNames().size()==0;
	}

	/**
	 * This is NOT backed by the properties.
	 */
	@Override
	public Set<String> keySet() {
		return new HashSet<String>(token.getPropertyNames());
	}

	@Override
	public Object put(String key, Object value) {
		List<TokenProperty> propTypes = MapTool.getCampaign().getCampaignProperties().getTokenPropertyList(token.getPropertyType());
		for(TokenProperty propType:propTypes) {
			if(propType.getName().equals(key)) {
				if(value!=null && !propType.getType().isInstance(value))
					throw new IllegalArgumentException("Given value is of type "+value.getClass()+" instead of "+propType.getType());
			}
		}
		Object old=token.setProperty(key, value);
		sendUpdate();
		return old;
	}

	private void sendUpdate() {
		Zone zone=MapTool.getFrame().getCurrentZoneRenderer().getZone();
		MapTool.serverCommand().putToken(zone.getId(), token);
		zone.putToken(token);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		List<TokenProperty> propTypes = MapTool.getCampaign().getCampaignProperties().getTokenPropertyList(token.getPropertyType());
		for(Entry<? extends String, ?> e:m.entrySet()) {
			for(TokenProperty propType:propTypes) {
				if(propType.getName().equals(e.getKey())) {
					if(e.getValue()!=null && !propType.getType().isInstance(e.getValue()))
						throw new IllegalArgumentException("Given value is of type "+e.getValue().getClass()+" instead of "+propType.getType());
				}
			}
			token.setProperty(e.getKey(), e.getValue());
		}
		sendUpdate();
	}

	@Override
	public Object remove(Object key) {
		if(key==null)
			throw new NullPointerException();
		Object o=token.getProperty(key.toString());
		token.resetProperty(key.toString());
		sendUpdate();
		return o;
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}
	
}
