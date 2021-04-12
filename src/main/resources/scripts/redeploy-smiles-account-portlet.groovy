package scripts

try {
  com.liferay.portal.deploy.DeployUtil.redeployTomcat('smiles-account-portlet');
} catch (Exception e) {
  e.printStackTrace();
}