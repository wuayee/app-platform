
/**
 * 3D demo, 为未来可以绘制3D内容做技术储备
 * 辉子 2021
 */
let threeDemo = (id, x, y, width, height, parent) => {
    let self = rectangle(id, x, y, width, height, parent, threeDemoDrawer);
    self.type = "threeDemo";
    self.text = "";
    self.fontColor = "dimgray";
    self.fontSize = 40;
    self.backColor = self.focusBackColor = "rgba(0,0,0,0.9)";

    let get = self.get;
    self.get = field => {
        if (field === "selectable") return true;
        else return get.call(self, field);
    };

    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        if (e.code === "KeyM") {
            self.drawer.changeMode();
            return false;
        }
        return keyPressed.apply(shape, [e]);
    }

    return self;
};

let threeDemoDrawer = (shape, div, x, y) => {

    let self = drawer(shape, div, x, y);
    const table = [
        "赵", "zhào", "中国百家姓", 1, 1,
        "钱", "qián", "中国百家姓", 18, 1,
        "孙", "sūn", "中国百家姓", 1, 2,
        "李", "lǐ", "中国百家姓", 2, 2,
        "周", "zhōu", "中国百家姓", 13, 2,
        "吴", "wú", "中国百家姓", 14, 2,
        "郑", "zhèng", "中国百家姓", 15, 2,
        "王", "wáng", "中国百家姓", 16, 2,
        "冯", "féng", "中国百家姓", 17, 2,
        "陈", "chén", "中国百家姓", 18, 2,
        "褚", "chǔ", "中国百家姓", 1, 3,
        "卫", "wèi", "中国百家姓", 2, 3,
        "蒋", "jiǎng", "中国百家姓", 13, 3,
        "沈", "shěn", "中国百家姓", 14, 3,
        "韩", "hán", "中国百家姓", 15, 3,
        "杨", "yáng", "中国百家姓", 16, 3,
        "朱", "zhū", "中国百家姓", 17, 3,
        "秦", "qín", "中国百家姓", 18, 3,
        "尤", "yóu", "中国百家姓", 1, 4,
        "许", "xǔ", "中国百家姓", 2, 4,
        "何", "hé", "中国百家姓", 3, 4,
        "吕", "lǚ", "中国百家姓", 4, 4,
        "施", "shī", "中国百家姓", 5, 4,
        "张", "zhāng", "中国百家姓", 6, 4,
        "孔", "kǒng", "中国百家姓", 7, 4,
        "曹", "cáo", "中国百家姓", 8, 4,
        "严", "yán", "中国百家姓", 9, 4,
        "华", "huà", "中国百家姓", 10, 4,
        "金", "jīn", "中国百家姓", 11, 4,
        "魏", "wèi", "中国百家姓", 12, 4,
        "陶", "táo", "中国百家姓", 13, 4,
        "姜", "jiāng", "中国百家姓", 14, 4,
        "戚", "qī", "中国百家姓", 15, 4,
        "谢", "xiè", "中国百家姓", 16, 4,
        "邹", "zōu", "中国百家姓", 17, 4,
        "喻", "yù", "中国百家姓", 18, 4,
        "柏", "bǎi", "中国百家姓", 1, 5,
        "水", "shuǐ", "中国百家姓", 2, 5,
        "窦", "dòu", "中国百家姓", 3, 5,
        "章", "zhāng", "中国百家姓", 4, 5,
        "云", "yún", "中国百家姓", 5, 5,
        "苏", "sū", "中国百家姓", 6, 5,
        "潘", "pān", "中国百家姓", 7, 5,
        "葛", "gě", "中国百家姓", 8, 5,
        "奚", "xī", "中国百家姓", 9, 5,
        "范", "fàn", "中国百家姓", 10, 5,
        "彭", "péng", "中国百家姓", 11, 5,
        "郎", "láng", "中国百家姓", 12, 5,
        "鲁", "lǔ", "中国百家姓", 13, 5,
        "韦", "wéi", "中国百家姓", 14, 5,
        "昌", "chāng", "中国百家姓", 15, 5,
        "马", "mǎ", "中国百家姓", 16, 5,
        "苗", "miáo", "中国百家姓", 17, 5,
        "凤", "fèng", "中国百家姓", 18, 5,
        "花", "huā", "中国百家姓", 1, 6,
        "方", "fāng", "中国百家姓", 2, 6,
        "俞", "yú", "中国百家姓", 4, 9,
        "任", "rèn", "中国百家姓", 5, 9,
        "袁", "yuán", "中国百家姓", 6, 9,
        "柳", "liǔ", "中国百家姓", 7, 9,
        "酆", "fēng", "中国百家姓", 8, 9,
        "鲍", "bào", "中国百家姓", 9, 9,
        "史", "shǐ", "中国百家姓", 10, 9,
        "唐", "táng", "中国百家姓", 11, 9,
        "费", "fèi", "中国百家姓", 12, 9,
        "廉", "lián", "中国百家姓", 13, 9,
        "岑", "cén", "中国百家姓", 14, 9,
        "薛", "xuē", "中国百家姓", 15, 9,
        "雷", "léi", "中国百家姓", 16, 9,
        "贺", "hè", "中国百家姓", 17, 9,
        "倪", "ní", "中国百家姓", 18, 9,
        "汤", "tāng", "中国百家姓", 4, 6,
        "滕", "téng", "中国百家姓", 5, 6,
        "殷", "yīn", "中国百家姓", 6, 6,
        "罗", "luó", "中国百家姓", 7, 6,
        "毕", "bì", "中国百家姓", 8, 6,
        "郝", "hǎo", "中国百家姓", 9, 6,
        "邬", "wū", "中国百家姓", 10, 6,
        "安", "ān", "中国百家姓", 11, 6,
        "常", "cháng", "中国百家姓", 12, 6,
        "乐", "yuè", "中国百家姓", 13, 6,
        "于", "yú", "中国百家姓", 14, 6,
        "时", "shí", "中国百家姓", 15, 6,
        "傅", "fù", "中国百家姓", 16, 6,
        "皮", "pí", "中国百家姓", 17, 6,
        "卞", "biàn", "中国百家姓", 18, 6,
        "齐", "qí", "中国百家姓", 1, 7,
        "康", "kāng", "中国百家姓", 2, 7,
        "伍", "wǔ", "中国百家姓", 4, 10,
        "余", "yú", "中国百家姓", 5, 10,
        "元", "yuán", "中国百家姓", 6, 10,
        "卜", "bǔ", "中国百家姓", 7, 10,
        "顾", "gù", "中国百家姓", 8, 10,
        "孟", "mèng", "中国百家姓", 9, 10,
        "平", "píng", "中国百家姓", 10, 10,
        "黄", "huáng", "中国百家姓", 11, 10,
        "和", "hé", "中国百家姓", 12, 10,
        "穆", "mù", "中国百家姓", 13, 10,
        "萧", "xiāo", "中国百家姓", 14, 10,
        "尹", "yǐn", "中国百家姓", 15, 10,
        "姚", "yáo", "中国百家姓", 16, 10,
        "邵", "shào", "中国百家姓", 17, 10,
        "湛", "zhàn", "中国百家姓", 18, 10,
        "汪", "wāng", "中国百家姓", 4, 7,
        "祁", "qí", "中国百家姓", 5, 7,
        "毛", "máo", "中国百家姓", 6, 7,
        "禹", "yǔ", "中国百家姓", 7, 7,
        "狄", "dí", "中国百家姓", 8, 7,
        "米", "mǐ", "中国百家姓", 9, 7,
        "贝", "bèi", "中国百家姓", 10, 7,
        "明", "míng", "中国百家姓", 11, 7,
        "臧", "zāng", "中国百家姓", 12, 7,
        "计", "jì", "中国百家姓", 13, 7,
        "伏", "fú", "中国百家姓", 14, 7,
        "成", "chéng", "中国百家姓", 15, 7,
        "戴", "dài", "中国百家姓", 16, 7,
        "谈", "tán", "中国百家姓", 17, 7,
        "宋", "sòng", "中国百家姓", 18, 7
    ];

    let camera, scene, renderer;
    let controls;

    const objects = [];
    const targets = { table: [], sphere: [], helix: [], grid: [] };

    init();
    animate();

    async function init() {

        camera = new PerspectiveCamera(40, self.parent.clientWidth / self.parent.clientHeight, 1, 10000);
        camera.position.z = 3000;

        scene = new Scene();

        // table

        for (let i = 0; i < table.length; i += 5) {

            const element = document.createElement('div');
            element.className = 'element';
            element.style.backgroundColor = 'rgba(0,127,127,' + (Math.random() * 0.5 + 0.25) + ')';
            const number = document.createElement('div');
            number.className = 'number';
            number.textContent = (i / 5) + 1;
            element.appendChild(number);

            const symbol = document.createElement('div');
            symbol.className = 'symbol';
            symbol.textContent = table[i];
            element.appendChild(symbol);

            const details = document.createElement('div');
            details.className = 'details';
            details.innerHTML = table[i + 1] + '<br>' + table[i + 2];
            element.appendChild(details);

            const objectCSS = new CSS3DObject(element);
            objectCSS.position.x = Math.random() * 4000 - 2000;
            objectCSS.position.y = Math.random() * 4000 - 2000;
            objectCSS.position.z = Math.random() * 4000 - 2000;
            scene.add(objectCSS);

            objects.push(objectCSS);

            //

            const object = new Object3D();
            object.position.x = (table[i + 3] * 140) - 1330;
            object.position.y = - (table[i + 4] * 180) + 990;

            targets.table.push(object);

        }

        // sphere

        const vector = new Vector3();

        for (let i = 0, l = objects.length; i < l; i++) {

            const phi = Math.acos(- 1 + (2 * i) / l);
            const theta = Math.sqrt(l * Math.PI) * phi;

            const object = new Object3D();

            object.position.setFromSphericalCoords(800, phi, theta);

            vector.copy(object.position).multiplyScalar(2);

            object.lookAt(vector);

            targets.sphere.push(object);

        }

        // helix

        for (let i = 0, l = objects.length; i < l; i++) {

            const theta = i * 0.175 + Math.PI;
            const y = - (i * 8) + 450;

            const object = new Object3D();

            object.position.setFromCylindricalCoords(900, theta, y);

            vector.x = object.position.x * 2;
            vector.y = object.position.y;
            vector.z = object.position.z * 2;

            object.lookAt(vector);

            targets.helix.push(object);

        }

        // grid

        for (let i = 0; i < objects.length; i++) {

            const object = new Object3D();

            object.position.x = ((i % 5) * 400) - 800;
            object.position.y = (- (Math.floor(i / 5) % 5) * 400) + 800;
            object.position.z = (Math.floor(i / 25)) * 1000 - 2000;

            targets.grid.push(object);

        }

        //

        renderer = new CSS3DRenderer();
        renderer.setSize(window.innerWidth, window.innerHeight);
        self.parent.appendChild(renderer.domElement);

        //

        controls = new TrackballControls(camera, renderer.domElement,shape.page.interactDrawer.canvas);
        controls.minDistance = 500;
        controls.maxDistance = 6000;
        controls.addEventListener('change', render);

        // transform(targets.table, 2000);
        //  await sleep(4000);
        // transform(targets.helix, 2000);
        // await sleep(6000);
        transform(targets.sphere, 2000);
        // await sleep(6000);
        // transform(targets.grid, 2000);
    }

    function transform(targets, duration) {

        TWEEN.removeAll();

        for (let i = 0; i < objects.length; i++) {

            const object = objects[i];
            const target = targets[i];

            new TWEEN.Tween(object.position)
                .to({ x: target.position.x, y: target.position.y, z: target.position.z }, Math.random() * duration + duration)
                .easing(TWEEN.Easing.Exponential.InOut)
                .start();

            new TWEEN.Tween(object.rotation)
                .to({ x: target.rotation.x, y: target.rotation.y, z: target.rotation.z }, Math.random() * duration + duration)
                .easing(TWEEN.Easing.Exponential.InOut)
                .start();

        }

        new TWEEN.Tween(this)
            .to({}, duration * 2)
            .onUpdate(render)
            .start();

    }


    function animate() {

        requestAnimationFrame(animate);

        TWEEN.update();

        controls.update();

    }

    function render() {

        renderer.render(scene, camera);

    }
    let modes = ["sphere", "helix", "grid"];// "table",
    let mode = modes[0];
    self.changeMode = () => {
        let idx = modes.indexOf(mode) + 1;
        if (idx === modes.length) idx = 0;
        mode = modes[idx];
        transform(targets[mode], 2000);
    };
    self.drawStatic = async () => {
        camera.aspect = self.parent.clientWidth / self.parent.clientHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(self.parent.clientWidth, self.parent.clientHeight);
        render();
        self.parent.style.background = shape.getBackColor();
    }


    return self;
};