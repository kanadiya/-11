import {createRouter, createWebHistory} from "vue-router";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // 登录页面
        {
            path: '/login',
            name: 'Login',
            component: () => import('../views/login/login.vue'),
            meta: {
                title: '登录',
                // 下面为公开路由功能，不需要登录能正常访问
                requiresAuth: false
            }
        },
        //注册
        {
            path: '/enroll',
            name: 'Enroll',
            component: () => import('../views/login/enroll.vue'),
            meta: {
                title: '注册',
                requiresAuth: false
            }
        },
        // 兼容旧跳转地址
        {
            path: '/login/enroll',
            redirect: '/enroll'
        },
        // 测试页面
        {
            path: '/test',
            name: 'Test',
            component: () => import('../views/test.vue'),
            meta: {
                title: '功能测试',
                requiresAuth: false
            }
        },
        // 首页
        {
            path: '/',
            name: 'Home',
            component: () => import('../views/HomeView.vue'),
            meta: {
                title: '运动会管理系统 - 首页',
                requiresAuth: true
            }
        },
        // 首页别名
        {
            path: '/dashboard',
            name: 'Dashboard',
            component: () => import('../views/HomeView.vue'),
            meta: {
                title: '运动会管理系统 - 首页',
                requiresAuth: true
            }
        },
        // 管理员路由
        {
            path: '/admin',
            name: 'Admin',
            component: () => import('../views/HomeView.vue'),
            meta: {
                title: '管理员面板',
                requiresAuth: true,
                role: 'ADMIN'
            },
            children: [
                {
                    path: 'dashboard',
                    name: 'AdminDashboard',
                    component: () => import('../views/admin/Dashboard.vue'),
                    meta: {
                        title: '数据概览',
                        requiresAuth: true,
                        role: 'ADMIN'
                    }
                },
                {
                    path: 'users',
                    name: 'UserManagement',
                    component: () => import('../views/admin/UserManagement.vue'),
                    meta: {
                        title: '用户管理',
                        requiresAuth: true,
                        role: 'ADMIN'
                    }
                },
                {
                    path: 'projects',
                    name: 'ProjectManagement',
                    component: () => import('../views/admin/ProjectManagement.vue'),
                    meta: {
                        title: '项目管理',
                        requiresAuth: true,
                        role: 'ADMIN'
                    }
                },
                {
                    path: 'schedules',
                    name: 'ScheduleManagement',
                    component: () => import('../views/admin/ScheduleManagement.vue'),
                    meta: {
                        title: '赛程编排',
                        requiresAuth: true,
                        role: 'ADMIN'
                    }
                },
                {
                    path: 'results',
                    name: 'ResultManagement',
                    component: () => import('../views/admin/ResultManagement.vue'),
                    meta: {
                        title: '成绩管理',
                        requiresAuth: true,
                        role: 'ADMIN'
                    }
                },
                {
                    path: 'announcements',
                    name: 'AnnouncementManagement',
                    component: () => import('../views/admin/AnnouncementManagement.vue'),
                    meta: {
                        title: '公告管理',
                        requiresAuth: true,
                        role: 'ADMIN'
                    }
                },
                {
                    path: 'referee-assignment',
                    name: 'RefereeAssignment',
                    component: () => import('../views/admin/RefereeAssignment.vue'),
                    meta: {
                        title: '裁判分配',
                        requiresAuth: true,
                        role: 'ADMIN'
                    }
                }
            ]
        },
        // 裁判路由
        {
            path: '/referee',
            name: 'Referee',
            component: () => import('../views/HomeView.vue'),
            meta: {
                title: '裁判面板',
                requiresAuth: true,
                role: 'REFEREE'
            },
            children: [
                {
                    path: 'tasks',
                    name: 'TaskManagement',
                    component: () => import('../views/referee/TaskManagement.vue'),
                    meta: {
                        title: '比赛任务',
                        requiresAuth: true,
                        role: 'REFEREE'
                    }
                },
                {
                    path: 'results',
                    name: 'RefereeResults',
                    component: () => import('../views/referee/ResultEntry.vue'),
                    meta: {
                        title: '成绩录入',
                        requiresAuth: true,
                        role: 'REFEREE'
                    }
                },
                {
                    path: 'appeals',
                    name: 'AppealManagement',
                    component: () => import('../views/referee/AppealManagement.vue'),
                    meta: {
                        title: '申诉处理',
                        requiresAuth: true,
                        role: 'REFEREE'
                    }
                },
                {
                    path: 'review',
                    name: 'RegistrationReview',
                    component: () => import('../views/referee/RegistrationReview.vue'),
                    meta: {
                        title: '报名审核',
                        requiresAuth: true,
                        role: 'REFEREE'
                    }
                }
            ]
        },
        // 用户路由
        {
            path: '/user',
            name: 'User',
            component: () => import('../views/HomeView.vue'),
            meta: {
                title: '用户面板',
                requiresAuth: true,
                role: 'USER'
            },
            children: [
                {
                    path: 'register',
                    name: 'CompetitionRegister',
                    component: () => import('../views/user/CompetitionRegister.vue'),
                    meta: {
                        title: '比赛报名',
                        requiresAuth: true,
                        role: 'USER'
                    }
                },
                {
                    path: 'schedule',
                    name: 'UserSchedule',
                    component: () => import('../views/user/UserSchedule.vue'),
                    meta: {
                        title: '我的赛程',
                        requiresAuth: true,
                        role: 'USER'
                    }
                },
                {
                    path: 'schedule-list',
                    name: 'ScheduleList',
                    component: () => import('../views/ScheduleView.vue'),
                    meta: {
                        title: '赛程列表',
                        requiresAuth: true,
                        role: 'USER'
                    }
                },
                {
                    path: 'results',
                    name: 'UserResults',
                    component: () => import('../views/user/UserResults.vue'),
                    meta: {
                        title: '我的成绩',
                        requiresAuth: true,
                        role: 'USER'
                    }
                },
                {
                    path: 'profile',
                    name: 'UserProfile',
                    component: () => import('../views/user/UserProfile.vue'),
                    meta: {
                        title: '个人信息',
                        requiresAuth: true,
                        role: 'USER'
                    }
                }
            ]
        },
        // 404页面
        {
            path: '/:pathMatch(.*)*',
            name: 'NotFound',
            component: () => import('../views/NotFound.vue'),
            meta: {
                title: '页面未找到',
                requiresAuth: false
            }
        }
    ]
});

// 路由守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    document.title = to.meta.title || '运动会管理系统';
    
    // 检查是否需要认证
    if (to.meta.requiresAuth) {
        // 从localStorage获取用户信息
        const userInfo = localStorage.getItem('userInfo');
        if (!userInfo) {
            // 未登录，跳转到登录页
            next('/login');
            return;
        }
        
        try {
            const user = JSON.parse(userInfo);
            
            // 检查角色权限
            if (to.meta.role && to.meta.role !== user.role) {
                // 权限不足，跳转到首页
                next('/');
                return;
            }
        } catch (error) {
            // 用户信息解析失败，跳转到登录页
            localStorage.removeItem('userInfo');
            next('/login');
            return;
        }
    }
    
    next();
});

export default router;