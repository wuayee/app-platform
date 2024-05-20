// 获取灵感大全最深层数
export const getDepth  =  (arr) => {
  const helper = (root) => {
    if (!root) return 0;
    if (!root.children) return 1;
    let ans = 0;
    for (let i = 0; i < root.children.length; ++i) {
      ans = Math.max(1 + helper(root.children[i]), ans);
    }
    return ans;
  };
  const dummyRoot = {
    id: "DummyRoot",
    children: arr
  };
  return helper(dummyRoot);
};

// 删除children为空
export const delNodeChild = (arr) => {
  arr.forEach(item => {
    if (item.children && item.children.length) {
      delNodeChild(item.children)
    } else {
      item.childrenEmpty = true;
    }
  })
  return arr
}

// 多维数组过滤
export const filterArr = (arr, parentId = undefined) => {
  return arr.reduce((prev, curr) => {
    if (parentId !== undefined) {
      Object.assign(curr, { 'parentId': parentId});
    }
    prev.push(curr);
    if (curr.children && curr.children.length) {
      prev.push(...filterArr(curr.children, curr.id));
    }
    return prev;
  }, []);
}

// 数组转树
export const arrayToTree = (arr) => {
  const rootNodes = [];
  const idMap = {};
  arr.map((node) => {
    node.children = [];
    idMap[node.id] = node;
  });
  arr.map((node) => {
    const { parentId } = node;
    if (parentId === undefined) {
      rootNodes.push(node);
    } else {
      idMap[parentId].children.push(node);
    }
  });
  return rootNodes;
}

// 递归获取第一个节点
export const getDeepNode = (list, func) => {
  for ( const node of list ) {
    if(func(node)) {
      return node
    }
    if (node.children.length) {
      const res = getDeepNode(node.children, func)
      if(res) {
        return res
      }
    }
  }
}