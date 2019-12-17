import com.liferay.portal.model.Group
 
groupId = 10282; //set your groupId here
oldTargetHost = 'pub-green.smiles.com.br';
newTargetHost = 'pub-blue.smiles.com.br';
 
Group g = com.liferay.portal.service.GroupLocalServiceUtil.getGroup(groupId)
 
g.setTypeSettings(g.getTypeSettings().replace('remoteAddress='+oldTargetHost, 'remoteAddress='+newTargetHost))
com.liferay.portal.service.GroupLocalServiceUtil.updateGroup(g)
 
println g