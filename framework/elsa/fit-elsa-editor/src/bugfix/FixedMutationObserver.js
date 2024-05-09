import MutationObserver from "@ckeditor/ckeditor5-engine/src/view/observer/mutationobserver";
import {getDataWithoutFiller, startsWithFiller} from "@ckeditor/ckeditor5-engine/src/view/filler";
import Selection from "@ckeditor/ckeditor5-engine/src/view/selection";

export default class FixedMutationObserver extends MutationObserver {
    constructor(view) {
        super(view);
    }

    _onMutations( domMutations ) {
        // As a result of this.flush() we can have an empty collection.
        if ( domMutations.length === 0 ) {
            return;
        }

        const domConverter = this.domConverter;

        // Use map and set for deduplication.
        const mutatedTexts = new Map();
        const mutatedElements = new Set();

        // Handle `childList` mutations first, so we will be able to check if the `characterData` mutation is in the
        // element with changed structure anyway.
        for ( const mutation of domMutations ) {
            if ( mutation.type === 'childList' ) {
                const element = domConverter.mapDomToView( mutation.target );

                // Do not collect mutations from UIElements and RawElements.
                if ( element && ( element.is( 'uiElement' ) || element.is( 'rawElement' ) ) ) {
                    continue;
                }

                if ( element && !this._isBogusBrMutation( mutation ) ) {
                    mutatedElements.add( element );
                }
            }
        }

        // Handle `characterData` mutations later, when we have the full list of nodes which changed structure.
        for ( const mutation of domMutations ) {
            const element = domConverter.mapDomToView( mutation.target );

            // Do not collect mutations from UIElements and RawElements.
            if ( element && ( element.is( 'uiElement' ) || element.is( 'rawElement' ) ) ) {
                continue;
            }

            if ( mutation.type === 'characterData' ) {
                const text = domConverter.findCorrespondingViewText( mutation.target );

                if ( text && !mutatedElements.has( text.parent ) ) {
                    // Use text as a key, for deduplication. If there will be another mutation on the same text element
                    // we will have only one in the map.
                    mutatedTexts.set( text, {
                        type: 'text',
                        oldText: text.data,
                        newText: getDataWithoutFiller( mutation.target ),
                        node: text
                    } );
                }
                    // When we added first letter to the text node which had only inline filler, for the DOM it is mutation
                    // on text, but for the view, where filler text node did not existed, new text node was created, so we
                // need to fire 'children' mutation instead of 'text'.
                else if ( !text && startsWithFiller( mutation.target ) ) {
                    mutatedElements.add( domConverter.mapDomToView( mutation.target.parentNode ) );
                }
            }
        }

        // Now we build the list of mutations to fire and mark elements. We did not do it earlier to avoid marking the
        // same node multiple times in case of duplication.

        // List of mutations we will fire.
        const viewMutations = [];

        for ( const mutatedText of mutatedTexts.values() ) {
            this.renderer.markToSync( 'text', mutatedText.node );
            viewMutations.push( mutatedText );
        }

        for ( const viewElement of mutatedElements ) {
            const domElement = domConverter.mapViewToDom( viewElement );
            const viewChildren = Array.from( viewElement.getChildren() );
            const newViewChildren = Array.from( domConverter.domChildrenToView( domElement, { withChildren: false } ) );

            // It may happen that as a result of many changes (sth was inserted and then removed),
            // both elements haven't really changed. #1031
            if ( !isEqualWith( viewChildren, newViewChildren, sameNodes ) ) {
                this.renderer.markToSync( 'children', viewElement );

                viewMutations.push( {
                    type: 'children',
                    oldChildren: viewChildren,
                    newChildren: newViewChildren,
                    node: viewElement
                } );
            }
        }

        // Retrieve `domSelection` using `ownerDocument` of one of mutated nodes.
        // There should not be simultaneous mutation in multiple documents, so it's fine.
        const domSelection = domMutations[ 0 ].target.ownerDocument.getSelection();

        let viewSelection = null;

        if ( domSelection && domSelection.anchorNode ) {
            // If `domSelection` is inside a dom node that is already bound to a view node from view tree, get
            // corresponding selection in the view and pass it together with `viewMutations`. The `viewSelection` may
            // be used by features handling mutations.
            // Only one range is supported.

            const viewSelectionAnchor = domConverter.domPositionToView( domSelection.anchorNode, domSelection.anchorOffset );
            const viewSelectionFocus = domConverter.domPositionToView( domSelection.focusNode, domSelection.focusOffset );

            // Anchor and focus has to be properly mapped to view.
            if ( viewSelectionAnchor && viewSelectionFocus ) {
                viewSelection = new Selection( viewSelectionAnchor );
                viewSelection.setFocus( viewSelectionFocus );
            }
        }

        // In case only non-relevant mutations were recorded it skips the event and force render (#5600).
        if ( viewMutations.length ) {
            this.document.fire( 'mutations', viewMutations, viewSelection );

            // If nothing changes on `mutations` event, at this point we have "dirty DOM" (changed) and de-synched
            // view (which has not been changed). In order to "reset DOM" we render the view again.
            this.view.forceRender();
        }

        function sameNodes( child1, child2 ) {
            // First level of comparison (array of children vs array of children) – use the Lodash's default behavior.
            if ( Array.isArray( child1 ) ) {
                return;
            }

            // Elements.
            if ( child1 === child2 ) {
                return true;
            }
            // Texts.
            else if ( child1.is( '$text' ) && child2.is( '$text' ) ) {
                return child1.data === child2.data;
            }

            // Not matching types.
            return false;
        }
    }
}