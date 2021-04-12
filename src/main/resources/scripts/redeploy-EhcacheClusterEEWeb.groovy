package scripts

try {
  com.liferay.portal.deploy.DeployUtil.redeployTomcat('ehcache-cluster-web');
} catch (Exception e) {
  e.printStackTrace();
}