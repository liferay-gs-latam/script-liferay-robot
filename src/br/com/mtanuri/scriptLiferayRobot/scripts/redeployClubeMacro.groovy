try {
  com.liferay.portal.deploy.DeployUtil.redeployTomcat('smiles-memberships-portlet');
} catch (Exception e) {
  e.printStackTrace();
}