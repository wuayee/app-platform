/**
 * 默认的空实现
 *
 * @return {{}}
 */
export const defaultRepository = () => {
    const self = {};


    /**
     * 保存graph.
     *
     * @param graphData graph数据.
     * @return {Promise<*>}
     */
    self.saveGraph = async graphData => {
    };

    /**
     * 获取graph.
     *
     * @param graphId graph的唯一标识.
     * @return {Promise<*>}
     */
    self.getGraph = async graphId => {
    }

    /**
     * 获取session.
     *
     * @return {Promise<*>}
     */
    self.getSession = async () => {
    }

    return self;
}