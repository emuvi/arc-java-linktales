function talesInsertHtmlAtCursor(html) {
    window.getSelection().deleteFromDocument();
    var range = window.getSelection().getRangeAt(0);
    var node = range.createContextualFragment(html);
    range.insertNode(node);
}

function talesChangeSelectionTree() {
    const sel = window.getSelection();
    var tree = '[ ' + sel.toString().length + ' ] < ';
    var node = sel.anchorNode;
    while (node) {
        tree = tree + node.nodeName.toLowerCase() + ' > ';
        node = node.parentNode;
    }
    talesBridge.changeSelectionTree(tree);
}

const talesLinkChange = {
    range: null,
    edit: false,
    anode: null
};

function talesFindNode(source, name) {
    var actual = source;
    while (actual != null) {
        if (actual.nodeName === name) {
            return actual;
        } else {
            actual = actual.parentNode;
        }
    }
    return null;
}

function talesStartLinkChange() {
    const sel = window.getSelection();
    talesLinkChange.range = sel.getRangeAt(0);
    talesLinkChange.edit = false;
    talesLinkChange.anode = null;
    if (sel) {
        if (sel.anchorNode) {
            linkNode = talesFindNode(sel.anchorNode, 'A');
            if (linkNode != null) {
                talesLinkChange.edit = true;
                talesLinkChange.anode = linkNode;
                var rangeLink = document.createRange();
                rangeLink.selectNodeContents(talesLinkChange.anode);
                talesLinkChange.range = rangeLink;
                sel.removeAllRanges();
                sel.addRange(rangeLink);
            }
        }
    }
    talesBridge.setSelectionText(talesLinkChange.range);
    if (talesLinkChange.edit) {
        talesBridge.setLinkSource(talesLinkChange.anode.href);
    } else {
        talesBridge.setLinkSource('');
    }
}

function talesUpdateLink(text, link) {
    if (talesLinkChange.edit) {
        talesLinkChange.anode.href = link;
        const range = talesLinkChange.range;
        range.deleteContents();
        var node = range.createContextualFragment(text);
        range.insertNode(node);
    } else {
        window.getSelection().deleteFromDocument();
        const range = talesLinkChange.range;
        var node = range.createContextualFragment('<a href="' + link + '">' + text + '</a>');
        range.insertNode(node);
        talesBindLinks();
    }
    window.getSelection().removeAllRanges();
    window.getSelection().addRange(talesLinkChange.range);
    talesLinkChange.range = null;
    talesLinkChange.edit = false;
    talesLinkChange.anode = null;
}

const talesSelectionChangesConfigs = { changes: 0, childList: true, attributes: true, characterData: true, subtree: true };

const talesSelectionChangesCallback = function (mutationsList, observer) {
    talesBridge.setChanges(talesSelectionChangesConfigs.changes);
    talesSelectionChangesConfigs.changes++;
};

const talesSelectionObserver = new MutationObserver(talesSelectionChangesCallback);

talesSelectionObserver.observe(document.body, talesSelectionChangesConfigs);

document.addEventListener('selectionchange', () => {
    talesBridge.selectionChange();
    talesChangeSelectionTree();
});

function talesResetChanges() {
    talesSelectionChangesConfigs.changes = 0;
}

function talesHandleURL(event) {
    event.preventDefault();
    if (event.ctrlKey) {
        linkNode = talesFindNode(event.srcElement, 'A');
        if (linkNode != null) {
            talesBridge.handleURL(linkNode.href);
        }
    }
}

function talesBindLinks() {
    const links = document.getElementsByTagName("A");
    for (i = 0; i < links.length; i++) {
        links[i].onclick = talesHandleURL;
    }
}