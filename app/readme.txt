把通知权限和精确闹钟打开后，点击下面那个按钮，会模拟出这个bug，然后捕获后会定精确闹钟1s再启动前台服务；

你可以通过是否启动了前台服务判断有用；log（testforeground筛选）可以看出来模拟出这个错了； ForegroundServiceStartNotAllowedException

上面那个按钮是模拟出错，不做处理，后台启动前台服务会直接崩溃的；（不用筛选log，在log里搜ForegroundServiceStartNotAllowedException就可以看到崩溃）


##修复背景：
线上部分用户>=12报少量的ForegroundServiceStartNotAllowedException问题，影响保活

处理方式：
1.针对>=12系统把这个exception catch住，然后采用精确闹钟定时1s后执行拉活
经验证，后台情况下闹钟收到后启动服务，是可以在后台启动前台服务的。